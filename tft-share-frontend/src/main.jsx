import { createRoot } from 'react-dom/client';
import App from './App.jsx';
import './index.css';
import { GoogleOAuthProvider } from '@react-oauth/google';
import axios from 'axios';

const clientId = '221594470812-2msddcnuckjr7iklc7j749epek0pbbjr.apps.googleusercontent.com';

// 환경별 axios 기본 URL 설정
const isDevelopment = import.meta.env.DEV;
if (isDevelopment) {
  // 로컬 개발 환경
  axios.defaults.baseURL = 'http://localhost:8080';
} else {
  // 배포 환경
  axios.defaults.baseURL = 'https://tftshare.com';
}

createRoot(document.getElementById('root')).render(
  <GoogleOAuthProvider clientId={clientId}>
    <App />
  </GoogleOAuthProvider>
);
