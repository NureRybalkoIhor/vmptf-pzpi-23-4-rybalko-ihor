import React, { useState, useEffect } from 'react';
import { HashRouter as Router, Routes, Route, NavLink, Navigate } from 'react-router-dom';
import Profile from './components/Profile';
import JsonParser from './components/JsonParser';
import Movies from './components/Movies';
import MovieDetail from './components/MovieDetail';
import './App.css';

const DEFAULT_MOVIES = [
  {
    id: '1',
    title: 'Початок (Inception)',
    review: 'Шедевр Крістофера Нолана. Фільм тримає в напрузі від першої до останньої хвилини. Ідея з маніпуляцією снами та багаторівневою реальністю виконана бездоганно. Саундтрек Ганса Ціммера створює неповторну атмосферу.',
    rating: 5
  },
  {
    id: '2',
    title: 'Інтерстеллар (Interstellar)',
    review: 'Неймовірний науково-фантастичний фільм про космічні подорожі, гравітаційні аномалії та силу людської любові. Наукова точність та візуальні ефекти чорної діри вражають уяву.',
    rating: 5
  }
];

function App() {
  const [movies, setMovies] = useState(() => {
    const saved = localStorage.getItem('vmtpf_movies');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        console.error('Не вдалося розпарсити збережені фільми', e);
      }
    }
    return DEFAULT_MOVIES;
  });

  useEffect(() => {
    localStorage.setItem('vmtpf_movies', JSON.stringify(movies));
  }, [movies]);

  const handleAddMovie = (newMovie) => {
    const movieWithId = {
      ...newMovie,
      id: String(Date.now())
    };
    setMovies((prevMovies) => [movieWithId, ...prevMovies]);
  };

  return (
    <Router>
      <div className="app-container">
        

        <header className="app-header">
          <div className="logo-container">
            <span className="logo-text">ПЗ2</span>
          </div>
          <nav className="nav-menu">
            <NavLink 
              to="/" 
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            >
              Профіль
            </NavLink>
            <NavLink 
              to="/parser" 
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            >
              Парсер JSON
            </NavLink>
            <NavLink 
              to="/movies" 
              className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}
            >
              Трекер фільмів
            </NavLink>
          </nav>
        </header>


        <main className="app-content">
          <Routes>
            <Route path="/" element={<Profile />} />
            <Route path="/parser" element={<JsonParser />} />
            <Route 
              path="/movies" 
              element={<Movies movies={movies} onAddMovie={handleAddMovie} />} 
            />
            <Route 
              path="/movies/:id" 
              element={<MovieDetail movies={movies} />} 
            />

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>

      </div>
    </Router>
  );
}

export default App;
