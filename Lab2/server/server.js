const express = require('express');
const cors = require('cors');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const products = require('./data/products');

const app = express();
const PORT = 5000;
const JWT_SECRET = 'SUPER_SECRET_OCEANIC_KEY_98765';

app.use(cors());
app.use(express.json());

const users = [];
const orders = [];
const purchaseHistories = {};

const hashedUserPass = bcrypt.hashSync('password123', 8);
users.push({
  id: 'user_1',
  email: 'user@store.com',
  password: hashedUserPass,
  name: 'Ігор Тестовий',
  isAdmin: false
});

const hashedAdminPass = bcrypt.hashSync('admin123', 8);
users.push({
  id: 'admin_1',
  email: 'admin@store.com',
  password: hashedAdminPass,
  name: 'Адміністратор',
  isAdmin: true
});

function authMiddleware(req, res, next) {
  const authHeader = req.headers['authorization'];
  if (!authHeader) {
    return res.status(401).json({ message: 'Токен відсутній' });
  }

  const token = authHeader.split(' ')[1];
  if (!token) {
    return res.status(401).json({ message: 'Невірний формат токену' });
  }

  try {
    const decoded = jwt.verify(token, JWT_SECRET);
    req.user = decoded;
    next();
  } catch (err) {
    return res.status(403).json({ message: 'Недійсний або прострочений токен' });
  }
}

app.get('/api/products', (req, res) => {
  const { category, search } = req.query;
  let filtered = [...products];

  if (category && category !== 'Всі') {
    filtered = filtered.filter(p => p.category === category);
  }

  if (search) {
    const query = search.toLowerCase();
    filtered = filtered.filter(p => 
      p.name.toLowerCase().includes(query) || 
      p.description.toLowerCase().includes(query)
    );
  }

  res.json(filtered);
});

app.get('/api/products/:id', (req, res) => {
  const product = products.find(p => p.id === req.params.id);
  if (!product) {
    return res.status(404).json({ message: 'Товар не знайдено' });
  }
  res.json(product);
});

app.post('/api/auth/register', (req, res) => {
  const { email, password, name } = req.body;
  if (!email || !password || !name) {
    return res.status(400).json({ message: 'Будь ласка, заповніть усі поля' });
  }

  const exists = users.find(u => u.email === email);
  if (exists) {
    return res.status(400).json({ message: 'Користувач з таким email вже існує' });
  }

  const hashedPassword = bcrypt.hashSync(password, 8);
  const newUser = {
    id: 'user_' + Date.now(),
    email,
    password: hashedPassword,
    name,
    isAdmin: email.startsWith('admin@')
  };

  users.push(newUser);

  const token = jwt.sign(
    { id: newUser.id, email: newUser.email, name: newUser.name, isAdmin: newUser.isAdmin },
    JWT_SECRET,
    { expiresIn: '24h' }
  );

  res.status(201).json({
    token,
    user: { id: newUser.id, email: newUser.email, name: newUser.name, isAdmin: newUser.isAdmin }
  });
});

app.post('/api/auth/login', (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ message: 'Введіть email та пароль' });
  }

  const user = users.find(u => u.email === email);
  if (!user) {
    return res.status(400).json({ message: 'Невірний email або пароль' });
  }

  const isPassValid = bcrypt.compareSync(password, user.password);
  if (!isPassValid) {
    return res.status(400).json({ message: 'Невірний email або пароль' });
  }

  const token = jwt.sign(
    { id: user.id, email: user.email, name: user.name, isAdmin: user.isAdmin },
    JWT_SECRET,
    { expiresIn: '24h' }
  );

  res.json({
    token,
    user: { id: user.id, email: user.email, name: user.name, isAdmin: user.isAdmin }
  });
});

app.post('/api/orders', authMiddleware, (req, res) => {
  const { items, shippingDetails, totalPrice } = req.body;
  if (!items || items.length === 0 || !shippingDetails) {
    return res.status(400).json({ message: 'Дані замовлення некоректні' });
  }

  const newOrder = {
    id: 'order_' + Date.now(),
    userId: req.user.id,
    userEmail: req.user.email,
    userName: req.user.name,
    items,
    shippingDetails,
    totalPrice,
    status: 'pending',
    createdAt: new Date().toISOString()
  };

  orders.push(newOrder);

  if (!purchaseHistories[req.user.id]) {
    purchaseHistories[req.user.id] = [];
  }
  
  items.forEach(item => {
    const product = products.find(p => p.id === item.id);
    if (product && !purchaseHistories[req.user.id].includes(product.category)) {
      purchaseHistories[req.user.id].push(product.category);
    }
  });

  res.status(201).json(newOrder);
});

app.get('/api/orders', authMiddleware, (req, res) => {
  if (req.user.isAdmin) {
    res.json(orders);
  } else {
    const userOrders = orders.filter(o => o.userId === req.user.id);
    res.json(userOrders);
  }
});

app.patch('/api/orders/:id/status', authMiddleware, (req, res) => {
  if (!req.user.isAdmin) {
    return res.status(403).json({ message: 'Доступ дозволено тільки адміністраторам' });
  }

  const { status } = req.body;
  const validStatuses = ['pending', 'processing', 'shipped', 'delivered'];
  if (!validStatuses.includes(status)) {
    return res.status(400).json({ message: 'Некоректний статус' });
  }

  const order = orders.find(o => o.id === req.params.id);
  if (!order) {
    return res.status(404).json({ message: 'Замовлення не знайдено' });
  }

  order.status = status;
  res.json(order);
});

app.get('/api/recommendations/:userId', authMiddleware, (req, res) => {
  const { userId } = req.params;
  
  if (req.user.id !== userId && !req.user.isAdmin) {
    return res.status(403).json({ message: 'Доступ заборонено' });
  }

  const userCategories = purchaseHistories[userId] || [];
  
  let recommended = [];

  if (userCategories.length > 0) {
    recommended = products.filter(p => userCategories.includes(p.category));
  } else {
    recommended = products.slice(0, 3);
  }

  res.json(recommended);
});

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
