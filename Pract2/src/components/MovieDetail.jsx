import React from 'react';
import { useParams, Link } from 'react-router-dom';

export default function MovieDetail({ movies }) {
  const { id } = useParams();
  
  const movie = movies.find((m) => String(m.id) === String(id));

  if (!movie) {
    return (
      <div className="movie-detail-wrapper fade-in">
        <div className="card error-card">
          <h3>Фільм не знайдено</h3>
          <p>На жаль, фільм із таким ідентифікатором не існує або був видалений.</p>
          <Link to="/movies" className="btn btn-primary margin-top">
            Повернутися до списку
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="movie-detail-wrapper fade-in">
      <div className="movie-detail-card">
        <div className="movie-detail-header">
          <h2>{movie.title}</h2>
          <div className="movie-detail-stars">
            {'★'.repeat(movie.rating) + '☆'.repeat(5 - movie.rating)}
            <span className="rating-badge-detail">{movie.rating} / 5</span>
          </div>
        </div>
        
        <div className="movie-detail-body">
          <h3>Рецензія</h3>
          <p className="movie-full-review">{movie.review}</p>
        </div>

        <div className="movie-detail-footer">
          <Link to="/movies" className="btn btn-secondary">
            Назад до списку
          </Link>
        </div>
      </div>
    </div>
  );
}
