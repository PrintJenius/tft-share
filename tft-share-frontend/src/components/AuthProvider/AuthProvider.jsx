import React, { createContext, useState, useEffect } from 'react';

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('jwtToken');
    setIsLoggedIn(!!token);
  }, []);

  const login = (token) => {
    localStorage.setItem('jwtToken', token);
    setIsLoggedIn(true);
  };

  const logout = () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('user');
    setIsLoggedIn(false);
  };

  return (
    <AuthContext.Provider value={{ isLoggedIn, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}
