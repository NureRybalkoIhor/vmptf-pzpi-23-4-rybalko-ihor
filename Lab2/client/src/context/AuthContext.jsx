import React, { createContext, useState, useEffect } from 'react';
import api from '../services/api';

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedToken = localStorage.getItem('lab2_token');
    const savedUser = localStorage.getItem('lab2_user');
    
    if (savedToken && savedUser) {
      setToken(savedToken);
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        console.error('Failed to parse user', e);
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const { token: receivedToken, user: receivedUser } = response.data;
      
      setToken(receivedToken);
      setUser(receivedUser);
      
      localStorage.setItem('lab2_token', receivedToken);
      localStorage.setItem('lab2_user', JSON.stringify(receivedUser));
      
      return { success: true };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Не вдалося увійти'
      };
    }
  };

  const register = async (email, password, name) => {
    try {
      const response = await api.post('/auth/register', { email, password, name });
      const { token: receivedToken, user: receivedUser } = response.data;
      
      setToken(receivedToken);
      setUser(receivedUser);
      
      localStorage.setItem('lab2_token', receivedToken);
      localStorage.setItem('lab2_user', JSON.stringify(receivedUser));
      
      return { success: true };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || 'Помилка реєстрації'
      };
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('lab2_token');
    localStorage.removeItem('lab2_user');
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
