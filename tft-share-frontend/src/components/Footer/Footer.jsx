import React from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import './Footer.css';

function Footer() {
  return (
    <footer className="footer">
      <Container fluid className="footer-container">
        <Row className="footer-content">
          {/* 회사 정보 */}
          <Col lg={4} md={6} className="footer-section">
            <div className="footer-brand">
              <h5 className="footer-logo">
                <span className="logo-text">TFT</span>
                <span className="logo-subtext">Share</span>
              </h5>
              <p className="footer-description">
                TFT 플레이어들을 위한 최고의 커뮤니티 플랫폼입니다. 
                전략을 공유하고, 피드백을 받고, 함께 성장하세요.
              </p>
            </div>
          </Col>
          
          {/* 연락처 */}
          <Col lg={2} md={6} className="footer-section">
            <h6 className="footer-title">연락처</h6>
            <div className="footer-contact">
              <p><i className="fas fa-envelope me-2"></i>uopl178@naver.com</p>
            </div>
          </Col>
        </Row>

        {/* 하단 구분선 */}
        <div className="footer-divider"></div>

        {/* 저작권 및 정책 */}
        <Row className="footer-bottom justify-content-center">
          <Col md={6} className="footer-copyright text-center">
            <p>&copy; 2025 TFT Share. All rights reserved.</p>            
          </Col>
        </Row>
      </Container>
    </footer>
  );
}

export default Footer;
