import { createRoot } from 'react-dom/client';
import App from './App.jsx';
import './index.css';
import { GoogleOAuthProvider } from '@react-oauth/google';

const clientId = '221594470812-2msddcnuckjr7iklc7j749epek0pbbjr.apps.googleusercontent.com';

createRoot(document.getElementById('root')).render(
  <GoogleOAuthProvider clientId={clientId}>
    <App />
  </GoogleOAuthProvider>
);
