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

          {/* 빠른 링크 */}
          <Col lg={2} md={6} className="footer-section">
            <h6 className="footer-title">빠른 링크</h6>
                         <ul className="footer-links">
               <li><a href="/videos">동영상 피드백</a></li>

             </ul>
          </Col>

          {/* 서비스 */}
          <Col lg={2} md={6} className="footer-section">
            <h6 className="footer-title">서비스</h6>
            <ul className="footer-links">
              <li><a href="/#">TFT 매칭</a></li>
              <li><a href="/#">성향 검사</a></li>
              <li><a href="/#">튜토리얼</a></li>
              <li><a href="/#">커뮤니티</a></li>
            </ul>
          </Col>

          {/* 지원 */}
          <Col lg={2} md={6} className="footer-section">
            <h6 className="footer-title">지원</h6>
            <ul className="footer-links">
              <li><a href="/#">고객센터</a></li>
              <li><a href="/#">자주 묻는 질문</a></li>
              <li><a href="/#">문의하기</a></li>
              <li><a href="/#">피드백</a></li>
            </ul>
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
        <Row className="footer-bottom">
          <Col md={6} className="footer-copyright">
            <p>&copy; 2025 TFT Share. All rights reserved.</p>
          </Col>
          <Col md={6} className="footer-policies">
            <ul className="policy-links">
              <li><a href="/#">개인정보처리방침</a></li>
              <li><a href="/#">이용약관</a></li>
              <li><a href="/#">쿠키 정책</a></li>
            </ul>
          </Col>
        </Row>
      </Container>
    </footer>
  );
}

export default Footer;
