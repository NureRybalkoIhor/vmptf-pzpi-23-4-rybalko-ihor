import React, { useState, useEffect, useMemo } from 'react';
import api from '../services/api';
import ProductCard from '../components/ProductCard';
import Recommendations from '../components/Recommendations';

const ITEMS_PER_PAGE = 4;
const CATEGORIES = ['Всі', 'Електроніка', 'Аксесуари', 'Стиль життя'];

export default function ProductList() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [selectedCategory, setSelectedCategory] = useState('Всі');
  const [searchQuery, setSearchQuery] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(searchQuery);
      setCurrentPage(1);
    }, 300);

    return () => clearTimeout(handler);
  }, [searchQuery]);

  useEffect(() => {
    setLoading(true);
    api.get('/products')
      .then((response) => {
        setProducts(response.data);
        setLoading(false);
      })
      .catch((err) => {
        setError('Не вдалося завантажити товари');
        setLoading(false);
      });
  }, []);

  const filteredProducts = useMemo(() => {
    return products.filter((product) => {
      const matchesCategory = selectedCategory === 'Всі' || product.category === selectedCategory;
      const matchesSearch = product.name.toLowerCase().includes(debouncedSearch.toLowerCase()) ||
                            product.description.toLowerCase().includes(debouncedSearch.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }, [products, selectedCategory, debouncedSearch]);

  const totalPages = Math.ceil(filteredProducts.length / ITEMS_PER_PAGE);
  const paginatedProducts = useMemo(() => {
    const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
    return filteredProducts.slice(startIndex, startIndex + ITEMS_PER_PAGE);
  }, [filteredProducts, currentPage]);

  const handleCategoryChange = (cat) => {
    setSelectedCategory(cat);
    setCurrentPage(1);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Завантаження каталогу товарів...</p>
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
    <div className="product-list-wrapper fade-in">
      <Recommendations />

      <div className="catalog-filters">
        <div className="search-box-container">
          <input
            type="text"
            placeholder="Пошук товарів..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
          />
          {searchQuery && (
            <button className="clear-search-btn" onClick={() => setSearchQuery('')}>×</button>
          )}
        </div>

        <div className="category-tabs">
          {CATEGORIES.map((cat) => (
            <button
              key={cat}
              onClick={() => handleCategoryChange(cat)}
              className={`category-tab-btn ${selectedCategory === cat ? 'active' : ''}`}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      <div className="catalog-section">
        <h3 className="section-title">Каталог товарів</h3>
        {paginatedProducts.length === 0 ? (
          <div className="card empty-catalog-card">
            <p>Жодного товару не знайдено за вашим запитом.</p>
          </div>
        ) : (
          <>
            <div className="products-grid">
              {paginatedProducts.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>

            {totalPages > 1 && (
              <div className="pagination-controls">
                <button
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                  className="btn btn-secondary btn-sm"
                >
                  Попередня
                </button>
                <span className="pagination-indicator">
                  Сторінка {currentPage} з {totalPages}
                </span>
                <button
                  disabled={currentPage === totalPages}
                  onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                  className="btn btn-secondary btn-sm"
                >
                  Наступна
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
