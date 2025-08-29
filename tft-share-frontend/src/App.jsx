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
import ChatBot from './components/ChatBot/ChatBot';


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
          
          {/* 전역 챗봇 - 모든 페이지에서 우측 하단에 표시 */}
          <ChatBot />
        </div>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
