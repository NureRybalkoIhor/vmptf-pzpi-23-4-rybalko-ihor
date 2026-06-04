import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { CartContext } from '../context/CartContext';

export default function Cart() {
  const { cart, updateQuantity, removeFromCart, clearCart, totalPrice } = useContext(CartContext);

  if (cart.length === 0) {
    return (
      <div className="cart-page-wrapper fade-in">
        <div className="card empty-cart-card">
          <h2>Кошик порожній</h2>
          <p>Додайте якісь товари з нашого каталогу, щоб розпочати покупки.</p>
          <Link to="/" className="btn btn-primary margin-top">
            Перейти до каталогу
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-page-wrapper fade-in">
      <div className="cart-layout">
        

        <div className="cart-items-section card">
          <div className="cart-header">
            <h3>Ваш кошик</h3>
            <button onClick={clearCart} className="btn btn-secondary btn-sm">
              Очистити кошик
            </button>
          </div>
          
          <div className="cart-items-list">
            {cart.map((item) => (
              <div key={item.id} className="cart-item">
                <div className="cart-item-img-wrapper">
                  <img src={item.image} alt={item.name} className="cart-item-img" />
                </div>
                <div className="cart-item-info">
                  <h4><Link to={`/products/${item.id}`}>{item.name}</Link></h4>
                  <span className="cart-item-cat">{item.category}</span>
                </div>
                <div className="cart-item-price-info">
                  <span className="cart-item-unit-price">{item.price} ₴ / шт</span>
                </div>
                <div className="cart-item-quantity-control">
                  <button 
                    onClick={() => updateQuantity(item.id, item.quantity - 1)}
                    className="qty-btn"
                  >
                    -
                  </button>
                  <span className="qty-value">{item.quantity}</span>
                  <button 
                    onClick={() => updateQuantity(item.id, item.quantity + 1)}
                    className="qty-btn"
                  >
                    +
                  </button>
                </div>
                <div className="cart-item-actions">
                  <span className="cart-item-subtotal">{item.price * item.quantity} ₴</span>
                  <button 
                    onClick={() => removeFromCart(item.id)}
                    className="remove-item-btn"
                    title="Видалити з кошика"
                  >
                    ×
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>


        <div className="cart-summary-section card">
          <h3>Підсумок</h3>
          <div className="summary-row">
            <span>Кількість товарів:</span>
            <span>{cart.reduce((sum, i) => sum + i.quantity, 0)} шт.</span>
          </div>
          <div className="summary-row total-price-row">
            <span>Загальна вартість:</span>
            <span>{totalPrice} ₴</span>
          </div>
          <div className="summary-actions">
            <Link to="/checkout" className="btn btn-primary btn-block text-center">
              Оформити замовлення
            </Link>
            <Link to="/" className="btn btn-secondary btn-block text-center">
              Продовжити покупки
            </Link>
          </div>
        </div>

      </div>
    </div>
  );
}
