import React, { useState, useContext, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

export default function Login() {
  const { login, user } = useContext(AuthContext);
  const navigate = useNavigate();
  const location = useLocation();

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

    const result = await login(email, password);
    if (!result.success) {
      setError(result.message);
      setLoading(false);
    }
  };

  return (
    <div className="auth-page-wrapper fade-in">
      <div className="auth-card card">
        <h3>Вхід в акаунт</h3>
        <p className="auth-subtitle">Здійсніть вхід для оформлення покупок та відстеження замовлень</p>
        
        {error && <div className="error-badge">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="auth-email">Електронна пошта (Email)</label>
            <input
              id="auth-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="user@store.com"
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
            <label htmlFor="auth-password">Пароль</label>
            <input
              id="auth-password"
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
            {loading ? 'Вхід...' : 'Увійти'}
          </button>
        </form>

        <div className="auth-footer-note">
          Немає акаунту? <Link to="/register">Зареєструватися</Link>
        </div>
        
        <div className="demo-credentials-box">
          <h5>Демо-акаунти для тесту:</h5>
          <p><strong>Покупець:</strong> user@store.com / password123</p>
          <p><strong>Адміністратор:</strong> admin@store.com / admin123</p>
        </div>
      </div>
    </div>
  );
}
