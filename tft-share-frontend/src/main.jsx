import { createRoot } from 'react-dom/client';
import App from './App.jsx';
import './index.css';
import { GoogleOAuthProvider } from '@react-oauth/google';
import axios from 'axios';

// 환경별 설정
const isDevelopment = import.meta.env.DEV;
const isProduction = import.meta.env.PROD;

// 환경별 Google Client ID 설정
const getGoogleClientId = () => {
  if (isDevelopment) {
    return '221594470812-2msddcnuckjr7iklc7j749epek0pbbjr.apps.googleusercontent.com';
  } else {
    // 운영 환경에서는 환경 변수나 설정 파일에서 가져와야 함
    return import.meta.env.VITE_GOOGLE_CLIENT_ID || '221594470812-2msddcnuckjr7iklc7j749epek0pbbjr.apps.googleusercontent.com';
  }
};

// 환경별 axios 기본 URL 설정
const getBackendUrl = () => {
  if (isDevelopment) {
    return import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';
  } else {
    // 운영 환경에서는 포트 번호 없이 사용 (443 포트)
    return import.meta.env.VITE_BACKEND_URL || 'https://tftshare.com';
  }
};

// axios 기본 설정
axios.defaults.baseURL = getBackendUrl();

// Google OAuth Provider 설정
const clientId = getGoogleClientId();

const root = createRoot(document.getElementById('root'));

root.render(
  <GoogleOAuthProvider clientId={clientId}>
    <App />
  </GoogleOAuthProvider>
);
