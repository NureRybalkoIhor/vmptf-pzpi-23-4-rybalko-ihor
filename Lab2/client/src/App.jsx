import React, { lazy, Suspense, useContext } from 'react';
import { HashRouter as Router, Routes, Route, NavLink, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import { CartProvider, CartContext } from './context/CartContext';
import PrivateRoute from './components/PrivateRoute';
import './App.css';

const ProductList = lazy(() => import('./pages/ProductList'));
const ProductDetail = lazy(() => import('./pages/ProductDetail'));
const Cart = lazy(() => import('./pages/Cart'));
const Checkout = lazy(() => import('./pages/Checkout'));
const Login = lazy(() => import('./pages/Login'));
const Register = lazy(() => import('./pages/Register'));
const Orders = lazy(() => import('./pages/Orders'));

function NavigationHeader() {
  const { user, logout } = useContext(AuthContext);
  const { totalItems } = useContext(CartContext);

  return (
    <header className="app-header">
      <div className="logo-container">
        <span className="logo-text">ЛР2 • Магазин</span>
      </div>
      <nav className="nav-menu">
        <NavLink to="/" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`} end>
          Каталог
        </NavLink>
        <NavLink to="/cart" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
          Кошик {totalItems > 0 && <span className="header-cart-badge">{totalItems}</span>}
        </NavLink>
        {user ? (
          <>
            <NavLink to="/orders" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
              {user.isAdmin ? 'Замовлення (Адмін)' : 'Мої замовлення'}
            </NavLink>
            <span className="user-greeting">Вітаємо, {user.name}</span>
            <button onClick={logout} className="btn btn-secondary btn-sm logout-btn">
              Вийти
            </button>
          </>
        ) : (
          <NavLink to="/login" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            Увійти
          </NavLink>
        )}
      </nav>
    </header>
  );
}

function AppContent() {
  return (
    <div className="app-container">
      <NavigationHeader />
      <main className="app-content">
        <Suspense
          fallback={
            <div className="loading-container">
              <div className="spinner"></div>
              <p>Завантаження сторінки...</p>
            </div>
          }
        >
          <Routes>
            <Route path="/" element={<ProductList />} />
            <Route path="/products/:id" element={<ProductDetail />} />
            <Route path="/cart" element={<Cart />} />
            
            {/* Private Routes */}
            <Route 
              path="/checkout" 
              element={
                <PrivateRoute>
                  <Checkout />
                </PrivateRoute>
              } 
            />
            <Route 
              path="/orders" 
              element={
                <PrivateRoute>
                  <Orders />
                </PrivateRoute>
              } 
            />

            {/* Auth Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            
            {/* Fallback to catalog */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Suspense>
      </main>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <Router>
          <AppContent />
        </Router>
      </CartProvider>
    </AuthProvider>
  );
}
