import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import Header from './components/Header/Header';
import Footer from './components/Footer/Footer';

import Home from './components/Home/Home';
import Videos from './pages/Videos/Videos';
import LoginSuccess from './components/LoginSuccess/LoginSuccess';
import { AuthProvider } from './components/AuthProvider/AuthProvider';
import VideoUpload from './pages/VideoUpload/VideoUpload';
import VideoDetail from './pages/VideoDetail/VideoDetail';
import UserProfile from './pages/UserProfile/UserProfile';


function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <div className="d-flex flex-column min-vh-100">
          <Header />

          <div className="flex-grow-1">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/videos" element={<Videos />} />
              <Route path="/video/:id" element={<VideoDetail />} />
              <Route path="/login-success" element={<LoginSuccess />} />
              <Route path="/upload" element={<VideoUpload />} />
              <Route path="/profile" element={<UserProfile />} />

            </Routes>
          </div>

          <Footer />
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
