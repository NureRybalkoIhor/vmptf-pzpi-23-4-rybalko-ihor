import React, { useState, useEffect, useContext } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../services/api';
import { CartContext } from '../context/CartContext';

export default function ProductDetail() {
  const { id } = useParams();
  const { addToCart } = useContext(CartContext);
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    api.get(`/products/${id}`)
      .then((response) => {
        setProduct(response.data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.response?.data?.message || 'Помилка завантаження товару');
        setLoading(false);
      });
  }, [id]);

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Завантаження інформації про товар...</p>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="card error-card">
        <h3>Товар не знайдено</h3>
        <p>{error || 'На жаль, такий товар відсутній у базі даних.'}</p>
        <Link to="/" className="btn btn-primary margin-top">
          Назад до каталогу
        </Link>
      </div>
    );
  }

  return (
    <div className="product-detail-wrapper fade-in">
      <div className="product-detail-container">
        <div className="product-detail-image-section">
          <img src={product.image} alt={product.name} className="product-detail-image" />
        </div>
        <div className="product-detail-info-section card">
          <span className="product-detail-category">{product.category}</span>
          <h2 className="product-detail-name">{product.name}</h2>
          <div className="product-detail-price">{product.price} ₴</div>
          <p className="product-detail-desc">{product.description}</p>
          
          <div className="product-detail-actions">
            <button onClick={() => addToCart(product)} className="btn btn-primary btn-block">
              Додати до кошика
            </button>
            <Link to="/" className="btn btn-secondary btn-block text-center">
              Назад до каталогу
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
