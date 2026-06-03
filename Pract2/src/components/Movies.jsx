import React, { useState } from 'react';
import { Link } from 'react-router-dom';

export default function Movies({ movies, onAddMovie }) {
  const [title, setTitle] = useState('');
  const [review, setReview] = useState('');
  const [rating, setRating] = useState(5);
  const [hoverRating, setHoverRating] = useState(0);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!title.trim() || !review.trim()) return;

    onAddMovie({
      title,
      review,
      rating
    });

    setTitle('');
    setReview('');
    setRating(5);
  };

  return (
    <div className="movies-wrapper fade-in">
      <div className="movies-layout">
        

        <div className="card movie-form-card">
          <h3>Додати фільм до трекеру</h3>
          <form onSubmit={handleSubmit} className="movie-form">
            <div className="form-group">
              <label htmlFor="movie-title">Назва фільму</label>
              <input
                id="movie-title"
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Введіть назву фільму..."
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="movie-review">Рецензія / Відгук</label>
              <textarea
                id="movie-review"
                value={review}
                onChange={(e) => setReview(e.target.value)}
                placeholder="Опишіть ваші враження..."
                required
              />
            </div>

            <div className="form-group">
              <label>Ваша оцінка</label>
              <div className="star-rating-container">
                {[1, 2, 3, 4, 5].map((star) => (
                  <span
                    key={star}
                    className={`star-icon ${
                      (hoverRating || rating) >= star ? 'selected' : ''
                    }`}
                    onClick={() => setRating(star)}
                    onMouseEnter={() => setHoverRating(star)}
                    onMouseLeave={() => setHoverRating(0)}
                  >
                    ★
                  </span>
                ))}
                <span className="rating-number">{rating} / 5</span>
              </div>
            </div>

            <button type="submit" className="btn btn-primary btn-block">
              Додати рецензію
            </button>
          </form>
        </div>


        <div className="movies-list-section">
          <h3>Список фільмів ({movies.length})</h3>
          {movies.length === 0 ? (
            <div className="card empty-movies-card">
              <p>Ви ще не додали жодного фільму.</p>
            </div>
          ) : (
            <div className="movies-grid">
              {movies.map((movie) => (
                <div key={movie.id} className="movie-card fade-in">
                  <div className="movie-card-header">
                    <h4>{movie.title}</h4>
                    <span className="movie-rating-badge">
                      {'★'.repeat(movie.rating) + '☆'.repeat(5 - movie.rating)}
                    </span>
                  </div>
                  <p className="movie-card-preview">
                    {movie.review.length > 100
                      ? `${movie.review.substring(0, 100)}...`
                      : movie.review}
                  </p>
                  <Link to={`/movies/${movie.id}`} className="btn btn-secondary btn-sm">
                    Детальніше
                  </Link>
                </div>
              ))}
            </div>
          )}
        </div>

      </div>
    </div>
  );
}
