import React from 'react';
import { Container, Row, Col, Card } from 'react-bootstrap';
import './TermsOfService.css';

const TermsOfService = () => {
  return (
    <div className="terms-of-service-container">
      <Container className="py-5">
        <Row className="justify-content-center">
          <Col lg={10}>
            <Card className="terms-of-service-card">
              <Card.Body className="p-5">
                <h1 className="text-center mb-5">서비스 이용약관</h1>
                
                <div className="terms-section mb-4">
                  <h2>제1조 (목적)</h2>
                  <p>
                    본 약관은 TFT Share(이하 "회사")가 제공하는 TFT 게임 영상 공유 및 
                    전략적 피드백 제공 서비스(이하 "서비스")의 이용과 관련하여 
                    회사와 이용자 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.
                  </p>
                </div>

                <div className="terms-section mb-4">
                  <h2>제2조 (정의)</h2>
                  <p>본 약관에서 사용하는 용어의 정의는 다음과 같습니다:</p>
                  <ul>
                    <li><strong>서비스:</strong> TFT 게임 영상 업로드, 공유, 피드백 제공 등</li>
                    <li><strong>이용자:</strong> 본 서비스를 이용하는 회원 및 비회원</li>
                    <li><strong>회원:</strong> 회사와 서비스 이용계약을 체결한 자</li>
                    <li><strong>콘텐츠:</strong> 이용자가 서비스에 업로드한 영상, 댓글, 피드백 등</li>
                  </ul>
                </div>

                <div className="terms-section mb-4">
                  <h2>제3조 (서비스의 제공)</h2>
                  <p>회사는 다음과 같은 서비스를 제공합니다:</p>
                  <ul>
                    <li>TFT 게임 영상 업로드 및 공유</li>
                    <li>타임라인 기반 전략적 피드백 시스템</li>
                    <li>커뮤니티 기능 (댓글, 좋아요 등)</li>
                    <li>사용자 프로필 및 티어 인증 시스템</li>
                  </ul>
                </div>

                <div className="terms-section mb-4">
                  <h2>제4조 (서비스 이용)</h2>
                  <p>
                    서비스 이용은 회사의 업무상 또는 기술상 특별한 지장이 없는 한 
                    연중무휴, 1일 24시간 운영을 원칙으로 합니다. 단, 회사는 
                    서비스의 운영상 필요한 경우 서비스 중단을 사전에 공지할 수 있습니다.
                  </p>
                </div>

                <div className="terms-section mb-4">
                  <h2>제5조 (회원가입 및 계정 관리)</h2>
                  <p>
                    서비스 이용을 원하는 자는 Google OAuth 2.0을 통해 회원가입을 할 수 있습니다. 
                    회원은 자신의 계정 정보를 안전하게 관리해야 하며, 
                    제3자에게 계정을 양도하거나 대여할 수 없습니다.
                  </p>
                </div>

                <div className="terms-section mb-4">
                  <h2>제6조 (콘텐츠 업로드 및 관리)</h2>
                  <p>이용자가 서비스에 업로드하는 콘텐츠는 다음 사항을 준수해야 합니다:</p>
                  <ul>
                    <li>저작권 및 지적재산권을 침해하지 않는 콘텐츠</li>
                    <li>타인의 명예를 훼손하거나 사생활을 침해하지 않는 콘텐츠</li>
                    <li>음란, 폭력, 불법적인 내용을 포함하지 않는 콘텐츠</li>
                    <li>서비스의 안정적 운영을 방해하지 않는 콘텐츠</li>
                  </ul>
                </div>

                <div className="terms-section mb-4">
                  <h2>제7조 (금지행위)</h2>
                  <p>이용자는 다음 행위를 해서는 안 됩니다:</p>
                  <ul>
                    <li>서비스의 정상적인 운영을 방해하는 행위</li>
                    <li>타인의 개인정보를 수집, 저장, 공개하는 행위</li>
                    <li>서비스를 통해 얻은 정보를 회사의 사전 승낙 없이 복제, 배포하는 행위</li>
                    <li>타인의 계정을 도용하거나 권한을 침해하는 행위</li>
                    <li>스팸, 광고성 정보를 전송하는 행위</li>
                  </ul>
                </div>

                <div className="terms-section mb-4">
                  <h2>제8조 (지적재산권)</h2>
                  <p>
                    서비스 내 모든 콘텐츠의 지적재산권은 회사 또는 해당 권리자에게 있습니다. 
                    이용자가 업로드한 콘텐츠에 대한 권리는 해당 이용자에게 있으며, 
                    회사는 서비스 제공을 위해 필요한 범위 내에서 해당 콘텐츠를 사용할 수 있습니다.
                  </p>
                </div>

                <div className="terms-section mb-4">
                  <h2>제9조 (책임제한)</h2>
                  <p>
                    회사는 천재지변, 전쟁, 기간통신사업자의 서비스 중단 등 
                    불가항력적인 사유로 서비스를 제공할 수 없는 경우 책임을 지지 않습니다. 
                    또한 이용자의 귀책사유로 인한 서비스 이용의 장애에 대해서도 책임을 지지 않습니다.
                  </p>
                </div>

                <div className="terms-section mb-4">
                  <h2>제10조 (서비스 이용제한)</h2>
                  <p>
                    회사는 이용자가 본 약관을 위반하거나 서비스의 정상적인 운영을 방해하는 경우, 
                    경고, 일시정지, 영구정지 등의 조치를 취할 수 있습니다.
                  </p>
                </div>

                <div className="terms-section mb-4">
                  <h2>제11조 (약관 변경)</h2>
                  <p>
                    회사는 필요한 경우 본 약관을 변경할 수 있으며, 
                    변경된 약관은 서비스 내 공지사항을 통해 사전에 고지합니다. 
                    이용자가 변경된 약관에 동의하지 않는 경우 서비스 이용을 중단할 수 있습니다.
                  </p>
                </div>

                <div className="terms-section mb-4">
                  <h2>제12조 (분쟁해결)</h2>
                  <p>
                    본 약관과 관련하여 회사와 이용자 간에 발생한 분쟁은 
                    대한민국 법률에 따라 해결하며, 
                    회사의 주소지 관할법원에서 해결합니다.
                  </p>
                </div>

                <div className="terms-section">
                  <h2>부칙</h2>
                  <p>본 약관은 2025년 1월 1일부터 시행됩니다.</p>
                </div>

                <div className="text-center mt-5">
                  <p className="text-muted">
                    서비스 이용약관에 대한 문의사항이 있으시면 언제든지 연락해 주시기 바랍니다.
                  </p>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default TermsOfService;
