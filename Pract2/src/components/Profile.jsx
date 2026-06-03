import React, { useState, useEffect } from 'react';

export default function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('/data.json')
      .then((response) => {
        if (!response.ok) {
          throw new Error('Не вдалося завантажити дані профілю');
        }
        return response.json();
      })
      .then((data) => {
        setProfile(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <div className="card loading-container">
        <div className="spinner"></div>
        <p>Завантаження профілю...</p>
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
    <div className="profile-wrapper fade-in">
      <div className="profile-card">
        <div className="profile-avatar">
          {profile?.name ? profile.name.split(' ').map(n => n[0]).join('') : 'P'}
        </div>
        <h2 className="profile-name">{profile?.name}</h2>
        <div className="profile-badge">Вік: {profile?.age} років</div>
        
        <div className="profile-hobbies-section">
          <h3>Мої захоплення</h3>
          <ul className="hobbies-list">
            {profile?.hobbies.map((hobby, index) => (
              <li key={index} className="hobby-item">
                <span className="hobby-bullet"></span>
                <span className="hobby-text">{hobby}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}
