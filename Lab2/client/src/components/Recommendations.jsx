import React, { useState, useEffect, useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import api from '../services/api';
import ProductCard from './ProductCard';

export default function Recommendations() {
  const { user } = useContext(AuthContext);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!user) return;

    setLoading(true);
    api.get(`/recommendations/${user.id}`)
      .then((response) => {
        setRecommendations(response.data);
        setLoading(false);
      })
      .catch((error) => {
        console.error('Failed to load recommendations', error);
        setLoading(false);
      });
  }, [user]);

  if (!user || loading || recommendations.length === 0) {
    return null;
  }

  return (
    <div className="recommendations-section fade-in">
      <h3 className="section-title">Рекомендовано для вас</h3>
      <div className="products-grid recommendations-grid">
        {recommendations.slice(0, 4).map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}
