import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

function LoginSuccess() {
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get('token');

    if (!token) {
      navigate('/login');
      return;
    }

    // 토큰 저장
    localStorage.setItem('jwtToken', token);

    // 서버에 토큰으로 유저 정보 요청
    axios.get('/api/users/me', {
      headers: { Authorization: `Bearer ${token}` }
    })
    .then(response => {
      const user = response.data;
      console.log('User info:', user);

      // 유저 정보를 JSON 문자열로 로컬스토리지에 저장
      localStorage.setItem('user', JSON.stringify(user));

      // 필요하면 상태 저장 후 홈으로 이동
      navigate('/');
    })
    .catch(error => {
      console.error('유저 정보 요청 실패:', error);
      navigate('/login');
    })
    .finally(() => setLoading(false));

  }, [location, navigate]);

  if (loading) return <div>로그인 중...</div>;

  return null;
}

export default LoginSuccess;
