// src/pages/VideoUpload.js
import React, { useState, useEffect } from "react";
import { Form, Button, Container, Row, Col, Card, Spinner, Badge } from "react-bootstrap";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./VideoUpload.css";

function VideoUpload() {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [file, setFile] = useState(null);
  const [timeline, setTimeline] = useState([{ start_time: 0, end_time: 0, content: "" }]);
  const [loading, setLoading] = useState(false);

  // 로그인 상태 확인
    useEffect(() => {
    const user = localStorage.getItem("user");
    const jwtToken = localStorage.getItem("jwtToken");
    
    if (!user || !jwtToken) {
      if (window.confirm("로그인이 필요합니다. 구글 로그인을 진행하시겠습니까?")) {
        const handleGoogleLogin = async () => {
          try {
            const response = await axios.get('/api/oauth2/auth-url');
            const { url } = response.data;
            window.location.href = url;
          } catch (error) {
            console.error('구글 로그인 URL을 가져오는데 실패했습니다.', error);
          }
        };
        handleGoogleLogin();
      }
      return;
    }
  }, [navigate]);

  // 초를 시간:분 형식으로 변환
  const formatTime = (seconds) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  };

  // 시간:분 형식을 초로 변환
  const parseTime = (timeString) => {
    if (!timeString || timeString === "" || typeof timeString !== 'string') {
      return 0;
    }
    
    const parts = timeString.split(':');
    if (parts.length !== 2) {
      return 0;
    }
    
    const minutes = parseInt(parts[0]) || 0;
    const seconds = parseInt(parts[1]) || 0;
    
    // 유효한 범위 체크
    if (minutes < 0 || seconds < 0 || seconds >= 60) {
      return 0;
    }
    
    return minutes * 60 + seconds;
  };

  // 타임라인 항목 추가
  const addTimelineItem = () => {
    if (!loading) {
      setTimeline([...timeline, { start_time: 0, end_time: 0, content: "" }]);
    }
  };

  // 타임라인 항목 삭제
  const removeTimelineItem = (index) => {
    if (!loading) {
      setTimeline(timeline.filter((_, i) => i !== index));
    }
  };

  // 타임라인 항목 값 변경
  const updateTimelineItem = (index, field, value) => {
    if (!loading) {
      const updated = [...timeline];
      updated[index][field] = value;
      setTimeline(updated);
    }
  };

  const handleUpload = async () => {
    const user = JSON.parse(localStorage.getItem("user")); 
    const userId = user?.id; // userId 대신 id 사용
    const jwtToken = localStorage.getItem("jwtToken");
    
    // 디버깅을 위한 로그
    console.log("User object:", user);
    console.log("User ID:", userId);
    console.log("JWT Token:", jwtToken);

    if (!file || !title) {
      alert("제목과 동영상 파일은 필수입니다.");
      return;
    }

    setLoading(true);
    try {
      // 타임라인 시간을 초 단위로 변환
      const convertedTimeline = timeline.map(item => {
        const startSeconds = parseTime(item.start_time);
        const endSeconds = parseTime(item.end_time);
        
        console.log('Original start_time:', item.start_time, '-> Parsed:', startSeconds);
        console.log('Original end_time:', item.end_time, '-> Parsed:', endSeconds);
        
        return {
          ...item,
          start_time: startSeconds,
          end_time: endSeconds
        };
      });

      console.log('Converted timeline:', convertedTimeline);

      const formData = new FormData();
      formData.append("userId", userId);
      formData.append("file", file);
      formData.append("title", title);
      formData.append("description", description);
      formData.append("timeline", JSON.stringify(convertedTimeline));

      await axios.post("/api/video/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          "Authorization": `Bearer ${jwtToken}`,
        },
      });

      alert("업로드 완료!");
      navigate("/videos");
    } catch (err) {
      console.error(err);
      
      // Google 인증 만료 오류 처리
      if (err.response && err.response.data && 
          err.response.data.includes("Google 인증이 만료되었습니다")) {
        alert("Google 인증이 만료되었습니다. 다시 로그인해주세요.");
        // 로그아웃 처리
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('user');
        // 로그인 페이지로 이동
        window.location.href = '/';
        return;
      }
      
      alert("업로드 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="video-upload-container">
      <Container className="video-upload-content">
        {/* 페이지 헤더 */}
        <div className="upload-header">
          <div className="header-content">
            <h1 className="page-title">동영상 업로드</h1>
            <p className="page-subtitle">
              TFT 전략을 동영상으로 공유하고 다른 플레이어들과 소통해보세요
            </p>
          </div>
        </div>

        {/* 동영상 정보 폼 */}
        <Card className="upload-form-card mb-4">
          <Card.Body>
            <div className="form-section-header">
              <h3 className="section-title">
                <i className="fas fa-info-circle me-2"></i>
                동영상 정보
              </h3>
              <p className="section-description">
                동영상의 제목과 설명을 입력해주세요
              </p>
            </div>

            <Row className="mb-3">
              <Col md={6}>
                <Form.Group>
                  <Form.Label className="form-label">제목 *</Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="동영상 제목을 입력하세요"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    disabled={loading}
                    className="form-input"
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group>
                  <Form.Label className="form-label">동영상 파일 *</Form.Label>
                  <Form.Control
                    type="file"
                    accept="video/*"
                    onChange={(e) => setFile(e.target.files[0])}
                    disabled={loading}
                    className="form-input"
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-4">
              <Form.Label className="form-label">설명</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                placeholder="동영상에 대한 설명을 입력하세요"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                disabled={loading}
                className="form-input"
              />
            </Form.Group>
          </Card.Body>
        </Card>

        {/* 타임라인 설정 */}
        <Card className="upload-form-card mb-4">
          <Card.Body>
            <div className="form-section-header">
              <h3 className="section-title">
                <i className="fas fa-clock me-2"></i>
                타임라인 설정
              </h3>
              <p className="section-description">
                동영상에서 중요한 순간들을 타임라인으로 표시하고 피드백을 받을 구간을 설정하세요
              </p>
            </div>

            <div className="timeline-container">
              {timeline.map((item, index) => (
                <div key={index} className="timeline-item">
                  <Row className="align-items-center">
                    <Col md={2} className="mb-3">
                      <Form.Label className="timeline-label">시작 시간</Form.Label>
                      <Form.Control
                        type="text"
                        placeholder="00:00"
                        value={item.start_time}
                        maxLength={5}
                        onChange={(e) => {
                          let timeValue = e.target.value;
                          
                          // 숫자만 입력된 경우 자동으로 : 추가
                          if (timeValue.length === 2 && /^\d{2}$/.test(timeValue)) {
                            timeValue = timeValue + ":";
                          }
                          
                          updateTimelineItem(index, "start_time", timeValue);
                        }}
                        disabled={loading}
                        className="timeline-input"
                      />
                    </Col>
                    <Col md={2} className="mb-3">
                      <Form.Label className="timeline-label">종료 시간</Form.Label>
                      <Form.Control
                        type="text"
                        placeholder="00:00"
                        value={item.end_time}
                        maxLength={5}
                        onChange={(e) => {
                          let timeValue = e.target.value;
                          
                          // 숫자만 입력된 경우 자동으로 : 추가
                          if (timeValue.length === 2 && /^\d{2}$/.test(timeValue)) {
                            timeValue = timeValue + ":";
                          }
                          
                          updateTimelineItem(index, "end_time", timeValue);
                        }}
                        disabled={loading}
                        className="timeline-input"
                      />
                    </Col>
                    <Col md={7} className="mb-3">
                      <Form.Label className="timeline-label">설명</Form.Label>
                      <Form.Control
                        type="text"
                        placeholder="이 구간에 대한 설명을 입력하세요"
                        value={item.content}
                        onChange={(e) => updateTimelineItem(index, "content", e.target.value)}
                        disabled={loading}
                        className="timeline-content-input"
                      />
                    </Col>
                    <Col md={1} className="mb-3 d-flex align-items-end">
                      <Button 
                        variant="outline-secondary" 
                        size="sm"
                        onClick={() => removeTimelineItem(index)}
                        disabled={loading}
                        className="remove-timeline-btn"
                      >
                        삭제
                      </Button>
                    </Col>
                  </Row>
                </div>
              ))}

              <div className="timeline-actions">
                <Button 
                  variant="outline-primary" 
                  onClick={addTimelineItem}
                  disabled={loading}
                  className="add-timeline-btn"
                >
                  <i className="fas fa-plus me-2"></i>
                  타임라인 항목 추가
                </Button>
              </div>
            </div>
          </Card.Body>
        </Card>

        {/* 업로드 버튼 */}
        <div className="upload-actions">
          <Button 
            onClick={() => navigate("/videos")}
            disabled={loading}
            className="cancel-btn me-3"
          >
            취소
          </Button>
          
          <Button 
            onClick={handleUpload} 
            disabled={loading}
            className="upload-btn"
          >
            {loading ? (
              <>
                <Spinner 
                  as="span"
                  animation="border"
                  size="sm"
                  role="status"
                  aria-hidden="true"
                  className="me-2"
                />
                업로드 중...
              </>
            ) : (
              <>
                <i className="fas fa-upload me-2"></i>
                업로드
              </>
            )}
          </Button>
        </div>
      </Container>
    </div>
  );
}

export default VideoUpload;
