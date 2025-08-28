import { createRoot } from 'react-dom/client';
import App from './App.jsx';
import './index.css';
import { GoogleOAuthProvider } from '@react-oauth/google';
import axios from 'axios';

const clientId = '221594470812-2msddcnuckjr7iklc7j749epek0pbbjr.apps.googleusercontent.com';

// axios 기본 URL 설정 (백엔드 서버)
axios.defaults.baseURL = 'http://tftshare.com:8080';

createRoot(document.getElementById('root')).render(
  <GoogleOAuthProvider clientId={clientId}>
    <App />
  </GoogleOAuthProvider>
);
