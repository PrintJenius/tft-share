import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Tab, Nav, Form, Button, Alert } from 'react-bootstrap';
import { useLocation } from 'react-router-dom';
import axios from 'axios';
import './UserProfile.css';

const UserProfile = () => {
  const location = useLocation();
  const [user, setUser] = useState(null);
  const [activeTab, setActiveTab] = useState('profile');
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({
    name: '',
    email: ''
  });
  const [tierVerification, setTierVerification] = useState({
    summonerName: '',
    region: 'KR'
  });

  const [message, setMessage] = useState({ type: '', text: '' });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchUserProfile();
    
    // URL 쿼리 파라미터에서 탭 정보 확인
    const urlParams = new URLSearchParams(location.search);
    const tabParam = urlParams.get('tab');
    
    if (tabParam === 'tier-verification') {
      setActiveTab('tier');
    }
  }, [location.search]);

  // 구글 로그인 함수
  const handleGoogleLogin = async () => {
    try {
      const response = await axios.get('/api/oauth2/auth-url');
      const { url } = response.data;
      window.location.href = url;
    } catch (error) {
      console.error('구글 로그인 URL을 가져오는데 실패했습니다.', error);
    }
  };

  const fetchUserProfile = async () => {
    try {
      const jwtToken = localStorage.getItem('jwtToken');
      if (!jwtToken) {
        if (window.confirm("로그인이 필요합니다. 구글 로그인을 진행하시겠습니까?")) {
          handleGoogleLogin();
        }
        return;
      }

      const userData = JSON.parse(localStorage.getItem('user'));
      
      if (userData) {
        setUser(userData);
        setEditForm({
          name: userData.name || '',
          email: userData.email || ''
        });
        setTierVerification({
          summonerName: userData.summonerName || '',
          region: 'KR'
        });
      }
    } catch (error) {
      console.error('사용자 정보 조회 중 오류:', error);
      setMessage({ type: 'danger', text: '사용자 정보를 불러오는 중 오류가 발생했습니다.' });
    }
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const jwtToken = localStorage.getItem('jwtToken');
      const response = await axios.put('/api/users/profile', editForm, {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });

      if (response.data) {
        // 로컬 스토리지 업데이트
        const updatedUser = { ...user, ...editForm };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        setUser(updatedUser);
        
        setMessage({ type: 'success', text: '프로필이 성공적으로 수정되었습니다.' });
        setIsEditing(false);
      }
    } catch (error) {
      console.error('프로필 수정 중 오류:', error);
      setMessage({ type: 'danger', text: '프로필 수정 중 오류가 발생했습니다.' });
    } finally {
      setLoading(false);
    }
  };

  const handleTierVerification = async (e) => {
    e.preventDefault();
    
    // 소환사명에 태그가 포함되어 있는지 확인
    if (!tierVerification.summonerName.includes('#')) {
      setMessage({ type: 'warning', text: '소환사명에 태그를 포함해주세요. (예: Jaebeob TFT#1111)' });
      return;
    }
    
    setLoading(true);
    
    try {
      const jwtToken = localStorage.getItem('jwtToken');
      const response = await axios.post('/api/users/verify-tier', tierVerification, {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });

      if (response.data.success) {
        // 로컬 스토리지 업데이트
        const updatedUser = { 
          ...user, 
          tier: response.data.tier,
          summonerName: response.data.summonerName,
          summonerVerified: true
        };
        localStorage.setItem('user', JSON.stringify(updatedUser));
        setUser(updatedUser);
        
        setMessage({ type: 'success', text: response.data.message });
      } else {
        setMessage({ type: 'danger', text: response.data.message });
      }
    } catch (error) {
      console.error('티어 인증 중 오류:', error);
      setMessage({ type: 'danger', text: '티어 인증 중 오류가 발생했습니다.' });
    } finally {
      setLoading(false);
    }
  };



  const getTierDisplayName = (tier) => {
    const tierNames = {
      'UNRANKED': '언랭크',
      'IRON': '아이언',
      'BRONZE': '브론즈',
      'SILVER': '실버',
      'GOLD': '골드',
      'PLATINUM': '플래티넘',
      'EMERALD': '에메랄드',
      'DIAMOND': '다이아몬드',
      'MASTER': '마스터',
      'GRANDMASTER': '그랜드마스터',
      'CHALLENGER': '챌린저'
    };
    return tierNames[tier] || tier;
  };

  if (!user) {
    return (
      <Container className="mt-4">
        <Alert variant="info">사용자 정보를 불러오는 중...</Alert>
      </Container>
    );
  }

  return (
    <Container className="user-profile-container">
      <Row className="justify-content-center">
        <Col lg={10}>
          <Card className="profile-card">
            <Card.Header className="profile-header">
              <div className="profile-info">
                <h2>{user.name}</h2>
                <p className="text-muted">{user.email}</p>
              </div>
            </Card.Header>

            <Card.Body>
              {message.text && (
                <Alert variant={message.type} dismissible onClose={() => setMessage({ type: '', text: '' })}>
                  {message.text}
                </Alert>
              )}

              <Tab.Container activeKey={activeTab} onSelect={(k) => setActiveTab(k)}>
                <Row>
                  <Col md={3}>
                    <Nav variant="pills" className="flex-column">
                      <Nav.Item>
                        <Nav.Link eventKey="profile" className="profile-nav-link">
                          <i className="fas fa-user me-2"></i>
                          기본 정보
                        </Nav.Link>
                      </Nav.Item>
                      <Nav.Item>
                        <Nav.Link eventKey="tier" className="profile-nav-link">
                          <i className="fas fa-trophy me-2"></i>
                          TFT 티어 인증
                        </Nav.Link>
                      </Nav.Item>

                    </Nav>
                  </Col>

                  <Col md={9}>
                    <Tab.Content>
                      {/* 기본 정보 탭 */}
                      <Tab.Pane eventKey="profile">
                        <div className="tab-content">
                          <h4>기본 정보</h4>
                          {!isEditing ? (
                            <div className="info-display">
                              <p><strong>이름:</strong> {user.name}</p>
                              <p><strong>이메일:</strong> {user.email}</p>
                                                             <Button 
                                 onClick={() => setIsEditing(true)}
                                 className="edit-btn"
                               >
                                 수정하기
                               </Button>
                            </div>
                          ) : (
                            <Form onSubmit={handleEditSubmit}>
                              <Form.Group className="mb-3">
                                <Form.Label>이름</Form.Label>
                                <Form.Control
                                  type="text"
                                  value={editForm.name}
                                  onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
                                  required
                                />
                              </Form.Group>
                              <Form.Group className="mb-3">
                                <Form.Label>이메일</Form.Label>
                                <Form.Control
                                  type="email"
                                  value={editForm.email}
                                  onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
                                  required
                                />
                              </Form.Group>
                              <div className="button-group">
                                <Button type="submit" variant="primary" disabled={loading}>
                                  {loading ? '저장 중...' : '저장'}
                                </Button>
                                                                 <Button 
                                   variant="secondary"
                                   onClick={() => setIsEditing(false)}
                                   className="ms-2"
                                 >
                                   취소
                                 </Button>
                              </div>
                            </Form>
                          )}
                        </div>
                      </Tab.Pane>

                      {/* TFT 티어 인증 탭 */}
                      <Tab.Pane eventKey="tier">
                        <div className="tab-content">
                          <h4>TFT 티어 인증</h4>
                          <div className="tier-info-section">
                            <div className="current-tier-info">
                              <h6>현재 티어 정보</h6>
                              <div className="tier-display">
                                <p><strong>티어:</strong> {getTierDisplayName(user.tier)}</p>
                                <p><strong>소환사명:</strong> {user.summonerName || '미설정'}</p>
                                <p><strong>인증 상태: </strong> 
                                  <span className={user.summonerVerified ? 'text-success' : 'text-warning'}>
                                    {user.summonerVerified ? '인증됨' : '미인증'}
                                  </span>
                                </p>
                              </div>
                            </div>
                            
                            <div className="tier-verification-section">
                              <h6>티어 인증하기</h6>
                              <p className="text-muted mb-3">
                                Riot Games API를 통해 실제 TFT 티어를 인증합니다.
                              </p>
                              <Form onSubmit={handleTierVerification}>
                                <Form.Group className="mb-3">
                                  <Form.Label>소환사명</Form.Label>
                                  <Form.Control
                                    type="text"
                                    placeholder="소환사명을 입력하세요 (예: Jaebeob TFT#1111)"
                                    value={tierVerification.summonerName}
                                    onChange={(e) => setTierVerification({ 
                                      ...tierVerification, 
                                      summonerName: e.target.value 
                                    })}
                                    required
                                  />
                                  <Form.Text className="text-muted">
                                    소환사명과 태그를 정확히 입력해주세요. (예: Jaebeob TFT#1111)
                                  </Form.Text>
                                </Form.Group>
                                
                                <Form.Group className="mb-3">
                                  <Form.Label>리전</Form.Label>
                                  <Form.Select
                                    value={tierVerification.region}
                                    onChange={(e) => setTierVerification({ 
                                      ...tierVerification, 
                                      region: e.target.value 
                                    })}
                                  >
                                    <option value="KR">한국 (KR)</option>
                                    <option value="NA">북미 (NA)</option>
                                    <option value="EUW">서유럽 (EUW)</option>
                                    <option value="EUNE">동유럽 (EUNE)</option>
                                  </Form.Select>
                                </Form.Group>
                                
                                <Button 
                                  type="submit" 
                                  variant="primary" 
                                  disabled={loading || !tierVerification.summonerName}
                                >
                                  {loading ? '인증 중...' : '티어 인증하기'}
                                </Button>
                              </Form>
                            </div>
                          </div>
                        </div>
                      </Tab.Pane>


                    </Tab.Content>
                  </Col>
                </Row>
              </Tab.Container>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default UserProfile;
