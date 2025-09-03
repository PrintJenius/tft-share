import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import { FaGoogle, FaUpload, FaSearch, FaLink, FaClock, FaUsers, FaStar } from 'react-icons/fa';
import axios from 'axios';
import './Home.css';

const Home = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const jwtToken = localStorage.getItem('jwtToken');
    setIsLoggedIn(!!jwtToken);
  }, []);

  const handleGoogleLogin = async () => {
    try {
      const response = await axios.get('/api/oauth2/auth-url');
      const { url } = response.data;
      window.location.href = url;
    } catch (error) {
      console.error('구글 로그인 URL을 가져오는데 실패했습니다.', error);
    }
  };

  const handleAccountLink = () => {
    navigate('/profile?tab=tier-verification');
  };

  return (
    <div className="home-container">
      <Container className="home-main-content">
        {/* 메인 히어로 섹션 */}
        <Card className="home-main-hero-card mb-5">
          <Card.Body className="home-hero-content text-center">
            <h1 className="home-hero-title">TFT Share</h1>
            <p className="home-hero-subtitle">
              TFT 게임 영상을 공유하고 전략적 피드백을 받아보세요.<br />
              실력 향상과 전략 공유를 위한 최고의 플랫폼입니다.
            </p>
            <div className="home-hero-buttons">
              <Button 
                variant="primary" 
                className="me-3"
                onClick={() => navigate('/upload')}
              >
                <FaUpload className="me-2" />
                동영상 업로드하기
              </Button>
              <Button 
                variant="primary"
                onClick={() => navigate('/videos')}
              >
                <FaSearch className="me-2" />
                동영상 둘러보기
              </Button>
            </div>
          </Card.Body>
        </Card>

        {/* 서비스 이용 방법 섹션 */}
        <h2 className="home-section-title text-center mb-5">서비스 이용 방법</h2>
        <Row className="mb-5">
          <Col md={4} className="mb-4">
            <Card className="home-how-to-card h-100">
              <Card.Body className="text-center">
                <div className="home-step-icon">
                  <FaUpload size={40} color="#667eea" />
                </div>
                <h5 className="card-title">1. 동영상 업로드</h5>
                <p className="card-text">
                  TFT 게임 영상을 업로드하고 타임라인을 설정하여 
                  피드백을 받을 구간을 지정합니다.
                </p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4} className="mb-4">
            <Card className="home-how-to-card h-100">
              <Card.Body className="text-center">
                <div className="home-step-icon">
                  <FaUsers size={40} color="#764ba2" />
                </div>
                <h5 className="card-title">2. 커뮤니티 참여</h5>
                <p className="card-text">
                  다른 사용자들의 영상을 시청하고 
                  전략적 피드백을 제공하여 함께 성장합니다.
                </p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4} className="mb-4">
            <Card className="home-how-to-card h-100">
              <Card.Body className="text-center">
                <div className="home-step-icon">
                  <FaStar size={40} color="#667eea" />
                </div>
                <h5 className="card-title">3. 전략 개선</h5>
                <p className="card-text">
                  받은 피드백을 바탕으로 전략을 개선하고 
                  더 나은 플레이어로 발전합니다.
                </p>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        {/* 계정 티어 인증 방법 섹션 */}
        <Card className="home-tier-verification-card mb-5">
          <Card.Body>
            <h2 className="home-section-title text-center mb-4">계정 티어 인증방법</h2>
            <div className="home-verification-steps">
              <div className="home-verification-step mb-4">
                <div className="home-step-number">1</div>
                <div className="home-step-content">
                  <h5>구글 계정으로 로그인</h5>
                  <p>간편한 구글 로그인으로 TFT Share 서비스를 이용할 수 있습니다.</p>
                </div>
              </div>
              
              <div className="home-verification-step mb-4">
                <div className="home-step-number">2</div>
                <div className="home-step-content">
                  <h5>계정 연동하기</h5>
                  <p>프로필에서 "계정 연동하기" 버튼을 클릭하여 TFT 계정을 연결합니다.</p>
                </div>
              </div>
              
              <div className="home-verification-step">
                <div className="home-step-number">3</div>
                <div className="home-step-content">
                  <h5>프로필에 티어 표시</h5>
                  <p>인증된 티어가 프로필과 동영상에 표시되어 신뢰도를 높입니다.</p>
                </div>
              </div>
            </div>
            
            <div className="text-center mt-4">
              <Button variant="primary" size="lg" onClick={handleAccountLink}>
                티어 인증하기
              </Button>
            </div>
          </Card.Body>
        </Card>

        {/* 피드백 받을 타임라인 설정 방법 섹션 */}
        <Card className="home-timeline-guide-card mb-5">
          <Card.Body>
            <Row>
              <Col md={6}>
                <div className="home-timeline-features">
                  <h5>타임라인 설정 기능</h5>
                  <ul className="home-feature-list">
                    <li>구간별 시간 설정</li>
                    <li>상세한 설명 추가</li>
                    <li>피드백 요청 구간 지정</li>
                    <li>전략적 판단 포인트 표시</li>
                  </ul>
                </div>
              </Col>
              <Col md={6}>
                <div className="home-timeline-example">
                  <h5>타임라인 예시</h5>
                  <div className="home-example-timeline">
                    <div className="home-timeline-item">
                      <div className="home-time">00:15</div>
                      <div className="home-description">첫 번째 라운드 시작</div>
                    </div>
                    <div className="home-timeline-item">
                      <div className="home-time">00:30</div>
                      <div className="home-description">챔피언 선택</div>
                    </div>
                    <div className="home-timeline-item">
                      <div className="home-time">01:00</div>
                      <div className="home-description">아이템 조합</div>
                    </div>
                  </div>
                </div>
              </Col>
            </Row>
          </Card.Body>
        </Card>

        {/* 서비스 특징 섹션 */}
        <Row className="mb-5">
          <Col md={4} className="mb-4">
            <Card className="home-feature-card h-100">
              <Card.Body className="text-center">
                <div className="home-feature-icon">
                  <FaClock size={30} color="#667eea" />
                </div>
                <h5 className="card-title">실시간 피드백</h5>
                <p className="card-text">
                  타임라인별로 구체적인 피드백을 받아 
                  게임 플레이를 개선할 수 있습니다.
                </p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4} className="mb-4">
            <Card className="home-feature-card h-100">
              <Card.Body className="text-center">
                <div className="home-feature-icon">
                  <FaUsers size={30} color="#764ba2" />
                </div>
                <h5 className="card-title">커뮤니티</h5>
                <p className="card-text">
                  TFT 플레이어들과 전략을 공유하고 
                  서로의 실력을 향상시킬 수 있습니다.
                </p>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4} className="mb-4">
            <Card className="home-feature-card h-100">
              <Card.Body className="text-center">
                <div className="home-feature-icon">
                  <FaStar size={30} color="#667eea" />
                </div>
                <h5 className="card-title">전략 분석</h5>
                <p className="card-text">
                  다양한 플레이어들의 전략을 분석하여 
                  새로운 전략을 배울 수 있습니다.
                </p>
              </Card.Body>
            </Card>
          </Col>
        </Row>

        {/* 시작하기 CTA 섹션 */}
        {!isLoggedIn && (
          <Card className="home-cta-card">
            <Card.Body className="text-center">
              <h2>지금 바로 TFT Share를 시작해보세요!</h2>
              <p className="mb-4">
                TFT 게임 영상을 공유하고 전략적 피드백을 받아보세요.<br />
                실력 향상과 전략 공유를 위한 최고의 플랫폼입니다.
              </p>
              <div className="home-cta-buttons">
                <Button variant="primary" onClick={handleGoogleLogin} className="me-3">
                  <FaGoogle className="me-2" />
                  구글로 로그인
                </Button>
              </div>
            </Card.Body>
          </Card>
        )}

        {/* 정책 및 약관 링크 섹션 */}
        <Card className="home-policy-links-card">
          <Card.Body className="text-center">
            <h3 className="mb-3">정책 및 약관</h3>
            <p className="text-muted mb-3">
              서비스 이용과 관련된 중요한 정보를 확인하세요.
            </p>
            <div className="policy-links">
              <Button 
                variant="outline-secondary" 
                href="/privacy-policy.html"
                target="_blank"
                className="me-3"
              >
                개인정보처리방침
              </Button>
              <Button 
                variant="outline-secondary" 
                href="/terms-of-service.html"
                target="_blank"
              >
                서비스 이용약관
              </Button>
            </div>
          </Card.Body>
        </Card>
      </Container>
    </div>
  );
};

export default Home;
