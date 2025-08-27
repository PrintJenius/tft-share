import React, { useContext } from 'react';
import { AuthContext } from '../AuthProvider/AuthProvider';
import { Navbar, Container, Nav, Button } from 'react-bootstrap';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import './Header.css';

function Header() {
  const { isLoggedIn, logout } = useContext(AuthContext);
  const location = useLocation();

  const handleGoogleLogin = async () => {
    try {
      const response = await axios.get('/api/oauth2/auth-url');
      const { url } = response.data;
      window.location.href = url;
    } catch (error) {
      console.error('구글 로그인 URL을 가져오는데 실패했습니다.', error);
    }
  };

  const handleLogout = () => {
    logout();
    window.location.href = '/';
  };

  // 현재 페이지에 따라 활성 탭 클래스 결정
  const getActiveClass = (path) => {
    return location.pathname === path ? 'nav-tab active' : 'nav-tab';
  };

  return (
    <Navbar className="header-navbar" expand="lg" sticky="top">
      <Container fluid className="header-container">
        {/* 로고 영역 */}
        <Navbar.Brand href="/" className="header-logo">
          <span className="logo-text">TFT</span>
          <span className="logo-subtext">Share</span>
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="basic-navbar-nav" className="header-toggle" />
        
        <Navbar.Collapse id="basic-navbar-nav" className="header-collapse">
          {/* 메인 네비게이션 */}
          <Nav className="header-nav me-auto">
            <Nav.Link href="/videos" className={getActiveClass('/videos')}>
              <i className="fas fa-video me-2"></i>
              동영상 피드백
            </Nav.Link>
            <Nav.Link href="/upload" className={getActiveClass('/upload')}>
              <i className="fas fa-upload me-2"></i>
              동영상 업로드
            </Nav.Link>

          </Nav>

          {/* 우측 사용자 메뉴 */}
          <Nav className="header-user-nav">
            {/* 사용자 상태에 따른 버튼 */}
            {isLoggedIn ? (
              <div className="user-menu">
                <Nav.Link href="/profile" className={getActiveClass('/profile')}>
                  <i className="fas fa-user me-2"></i>
                  내 정보
                </Nav.Link>
                <Button variant="outline-danger" className="logout-btn" onClick={handleLogout}>
                  로그아웃
                </Button>
              </div>
            ) : (
              <div className="auth-buttons">
                <Button variant="primary" className="signup-btn" onClick={handleGoogleLogin}>
                  <i className="fab fa-google me-2"></i>
                  Google 로그인
                </Button>
              </div>
            )}
          </Nav>
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
}

export default Header;
