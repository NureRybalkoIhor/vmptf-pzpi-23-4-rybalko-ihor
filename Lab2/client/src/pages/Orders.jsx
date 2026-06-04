import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import api from '../services/api';

const STATUS_LABELS = {
  pending: { text: 'Очікує підтвердження', class: 'status-pending' },
  processing: { text: 'В обробці', class: 'status-processing' },
  shipped: { text: 'Відправлено', class: 'status-shipped' },
  delivered: { text: 'Доставлено', class: 'status-delivered' }
};

export default function Orders() {
  const { user } = useContext(AuthContext);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchOrders = () => {
    setLoading(true);
    api.get('/orders')
      .then((response) => {
        const sorted = response.data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        setOrders(sorted);
        setLoading(false);
      })
      .catch((err) => {
        setError('Не вдалося завантажити замовлення');
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleUpdateStatus = async (orderId, newStatus) => {
    try {
      await api.patch(`/orders/${orderId}/status`, { status: newStatus });
      setOrders(prevOrders => 
        prevOrders.map(o => o.id === orderId ? { ...o, status: newStatus } : o)
      );
    } catch (err) {
      alert('Помилка оновлення статусу замовлення');
    }
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Завантаження списку замовлень...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card error-card">
        <h3>Помилка</h3>
        <p>{error}</p>
      </div>
    );
  }

  return (
    <div className="orders-page-wrapper fade-in">
      <h2 className="page-title">{user.isAdmin ? 'Панель керування замовленнями (Адмін)' : 'Мої замовлення'}</h2>
      
      {orders.length === 0 ? (
        <div className="card empty-orders-card text-center">
          <p>Немає оформлених замовлень.</p>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order) => {
            const statusConfig = STATUS_LABELS[order.status] || { text: order.status, class: '' };
            const formattedDate = new Date(order.createdAt).toLocaleDateString('uk-UA', {
              year: 'numeric',
              month: 'long',
              day: 'numeric',
              hour: '2-digit',
              minute: '2-digit'
            });

            return (
              <div key={order.id} className="order-item card">
                <div className="order-item-header">
                  <div>
                    <span className="order-id">№ {order.id.replace('order_', '')}</span>
                    <span className="order-date">{formattedDate}</span>
                  </div>
                  <span className={`order-status-badge ${statusConfig.class}`}>
                    {statusConfig.text}
                  </span>
                </div>

                {user.isAdmin && (
                  <div className="order-user-details">
                    <p><strong>Покупець:</strong> {order.userName} ({order.userEmail})</p>
                  </div>
                )}

                <div className="order-products">
                  <h5>Товари:</h5>
                  <ul className="order-products-list">
                    {order.items.map((item, idx) => (
                      <li key={idx} className="order-product-row">
                        <span>{item.name} (×{item.quantity})</span>
                        <span>{item.price * item.quantity} ₴</span>
                      </li>
                    ))}
                  </ul>
                </div>

                <div className="order-shipping-details">
                  <h5>Адреса доставки:</h5>
                  <p>{order.shippingDetails.name}, {order.shippingDetails.phone}</p>
                  <p className="shipping-address-text">{order.shippingDetails.address}</p>
                </div>

                <div className="order-item-footer">
                  <span className="order-total-price">Загальна сума: <strong>{order.totalPrice} ₴</strong></span>

                  {user.isAdmin && (
                    <div className="admin-status-actions">
                      <span className="admin-actions-label">Змінити статус:</span>
                      <div className="status-buttons-tray">
                        {['pending', 'processing', 'shipped', 'delivered'].map((st) => (
                          <button
                            key={st}
                            onClick={() => handleUpdateStatus(order.id, st)}
                            disabled={order.status === st}
                            className={`btn btn-secondary btn-sm status-tray-btn ${
                              order.status === st ? 'active-status-btn' : ''
                            }`}
                          >
                            {st === 'pending' && 'Нове'}
                            {st === 'processing' && 'Обробка'}
                            {st === 'shipped' && 'Відправлено'}
                            {st === 'delivered' && 'Доставлено'}
                          </button>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
