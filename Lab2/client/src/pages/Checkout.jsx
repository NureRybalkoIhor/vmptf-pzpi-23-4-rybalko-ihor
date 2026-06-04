import React, { useState, useContext } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { CartContext } from '../context/CartContext';
import api from '../services/api';

export default function Checkout() {
  const { cart, totalPrice, clearCart } = useContext(CartContext);
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [address, setAddress] = useState('');
  const [phone, setPhone] = useState('');
  
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (cart.length === 0) return;

    setSubmitting(true);
    setError('');

    try {
      await api.post('/orders', {
        items: cart.map((item) => ({
          id: item.id,
          name: item.name,
          price: item.price,
          quantity: item.quantity
        })),
        shippingDetails: { name, address, phone },
        totalPrice
      });

      clearCart();
      navigate('/orders');
    } catch (err) {
      setError(err.response?.data?.message || 'Помилка при оформленні замовлення. Спробуйте ще раз.');
      setSubmitting(false);
    }
  };

  if (cart.length === 0) {
    return (
      <div className="checkout-page-wrapper fade-in">
        <div className="card empty-checkout-card text-center">
          <h2>У кошику немає товарів</h2>
          <p>Не можна оформити порожнє замовлення.</p>
          <Link to="/" className="btn btn-primary margin-top">
            Перейти до каталогу
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="checkout-page-wrapper fade-in">
      <div className="checkout-layout">
        

        <div className="checkout-order-review card">
          <h3>Ваше замовлення</h3>
          <div className="checkout-items-summary">
            {cart.map((item) => (
              <div key={item.id} className="checkout-item-summary-row">
                <span className="summary-item-name">{item.name} <strong>× {item.quantity}</strong></span>
                <span className="summary-item-price">{item.price * item.quantity} ₴</span>
              </div>
            ))}
          </div>
          <div className="checkout-total-row">
            <span>До оплати:</span>
            <span>{totalPrice} ₴</span>
          </div>
        </div>


        <div className="checkout-form-card card">
          <h3>Оформлення доставки</h3>
          {error && <div className="error-badge">{error}</div>}
          
          <form onSubmit={handleSubmit} className="checkout-form">
            <div className="form-group">
              <label htmlFor="shipping-name">ПІБ одержувача</label>
              <input
                id="shipping-name"
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Ігор Рибалко"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="shipping-address">Адреса доставки</label>
              <input
                id="shipping-address"
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                placeholder="м. Харків, вул. Науки, буд. 10, кв. 5"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="shipping-phone">Контактний телефон</label>
              <input
                id="shipping-phone"
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="+380991234567"
                required
              />
            </div>

            <div className="form-actions">
              <button 
                type="submit" 
                disabled={submitting} 
                className="btn btn-primary btn-block"
              >
                {submitting ? 'Оформлення...' : 'Підтвердити замовлення'}
              </button>
              <Link to="/cart" className="btn btn-secondary btn-block text-center">
                Повернутися до кошика
              </Link>
            </div>
          </form>
        </div>

      </div>
    </div>
  );
}
