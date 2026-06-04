import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { CartContext } from '../context/CartContext';

export default function ProductCard({ product }) {
  const { addToCart } = useContext(CartContext);

  return (
    <div className="product-card fade-in">
      <div className="product-card-image-wrapper">
        <img src={product.image} alt={product.name} className="product-card-image" />
        <span className="product-card-category">{product.category}</span>
      </div>
      <div className="product-card-content">
        <h4 className="product-card-title">
          <Link to={`/products/${product.id}`}>{product.name}</Link>
        </h4>
        <p className="product-card-desc">
          {product.description.length > 80
            ? `${product.description.substring(0, 80)}...`
            : product.description}
        </p>
        <div className="product-card-footer">
          <span className="product-card-price">{product.price} ₴</span>
          <button onClick={() => addToCart(product)} className="btn btn-primary btn-sm">
            У кошик
          </button>
        </div>
      </div>
    </div>
  );
}
