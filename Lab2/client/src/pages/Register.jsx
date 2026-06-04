import React, { useState, useContext, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export default function Register() {
  const { register, user } = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const from = location.state?.from?.pathname || '/';

  useEffect(() => {
    if (user) {
      navigate(from, { replace: true });
    }
  }, [user, navigate, from]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    const result = await register(email, password, name);
    if (!result.success) {
      setError(result.message);
      setLoading(false);
    }
  };

  return (
    <div className="auth-page-wrapper fade-in">
      <div className="auth-card card">
        <h3>Реєстрація профілю</h3>
        <p className="auth-subtitle">Створіть свій обліковий запис для швидкого замовлення</p>
        
        {error && <div className="error-badge">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="register-name">Ваше ім'я</label>
            <input
              id="register-name"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Ігор Рибалко"
              required
              style={{
                width: '100%',
                padding: '14px 16px',
                background: 'rgba(255, 255, 255, 0.8)',
                border: '1px solid var(--glass-border)',
                borderRadius: '12px',
                fontSize: '15px'
              }}
            />
          </div>

          <div className="form-group">
            <label htmlFor="register-email">Електронна пошта (Email)</label>
            <input
              id="register-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="email@example.com"
              required
              style={{
                width: '100%',
                padding: '14px 16px',
                background: 'rgba(255, 255, 255, 0.8)',
                border: '1px solid var(--glass-border)',
                borderRadius: '12px',
                fontSize: '15px'
              }}
            />
          </div>

          <div className="form-group">
            <label htmlFor="register-password">Пароль</label>
            <input
              id="register-password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
              style={{
                width: '100%',
                padding: '14px 16px',
                background: 'rgba(255, 255, 255, 0.8)',
                border: '1px solid var(--glass-border)',
                borderRadius: '12px',
                fontSize: '15px'
              }}
            />
          </div>

          <button type="submit" disabled={loading} className="btn btn-primary btn-block">
            {loading ? 'Створення профілю...' : 'Зареєструватися'}
          </button>
        </form>

        <div className="auth-footer-note">
          Вже маєте акаунт? <Link to="/login">Увійти</Link>
        </div>
      </div>
    </div>
  );
}
