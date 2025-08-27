import React, { useState, useEffect, useRef } from "react";
import { Container, Row, Col, Card, Form, Button, Modal, Badge } from "react-bootstrap";
import { useParams } from "react-router-dom";
import axios from "axios";
import "./VideoDetail.css";

function VideoDetail() {
  const { id } = useParams(); // URL 파라미터에서 video id 가져오기
  const [video, setVideo] = useState(null);
  const [timeline, setTimeline] = useState([]);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [loading, setLoading] = useState(true);
  const [feedbacks, setFeedbacks] = useState({}); // 타임라인별 피드백 수
  const [feedbackLists, setFeedbackLists] = useState({}); // 타임라인별 피드백 목록
  const [showFeedbackModal, setShowFeedbackModal] = useState(false);
  const [selectedTimeline, setSelectedTimeline] = useState(null);
  const [newFeedback, setNewFeedback] = useState("");
  const [showFeedbackInputs, setShowFeedbackInputs] = useState({}); // 각 타임라인별 피드백 입력창 표시 여부
  const [newFeedbackTexts, setNewFeedbackTexts] = useState({}); // 각 타임라인별 새 피드백 텍스트
  const [editingFeedbacks, setEditingFeedbacks] = useState({}); // 수정 중인 피드백 ID
  const [editFeedbackTexts, setEditFeedbackTexts] = useState({}); // 수정 중인 피드백 텍스트
  const [videoStats, setVideoStats] = useState({ views: 0, likes: 0, isLikedByUser: false });
  const iframeRef = useRef(null);

  const jwtToken = localStorage.getItem("jwtToken");

  // 티어 표시 함수
  const getTierDisplayName = (tier) => {
    const tierInfo = {
      'UNRANKED': { name: '언랭크', color: '#6c757d' },
      'IRON': { name: '아이언', color: '#5d4037' },
      'BRONZE': { name: '브론즈', color: '#8d6e63' },
      'SILVER': { name: '실버', color: '#9e9e9e' },
      'GOLD': { name: '골드', color: '#ffd700' },
      'PLATINUM': { name: '플래티넘', color: '#00bcd4' },
      'EMERALD': { name: '에메랄드', color: '#4caf50' },
      'DIAMOND': { name: '다이아몬드', color: '#9c27b0' },
      'MASTER': { name: '마스터', color: '#ff9800' },
      'GRANDMASTER': { name: '그랜드마스터', color: '#f44336' },
      'CHALLENGER': { name: '챌린저', color: '#e91e63' }
    };
    
    const tierData = tierInfo[tier] || tierInfo['UNRANKED'];
    
    return (
      <span style={{ color: tierData.color, fontWeight: 'bold' }}>
        {tierData.name}
      </span>
    );
  };

  useEffect(() => {
    const fetchVideoDetail = async () => {
      try {
        // JWT 토큰이 있으면 헤더에 포함, 없으면 헤더 없이 요청
        const headers = jwtToken ? {
          Authorization: `Bearer ${jwtToken}`,
        } : {};

        const res = await axios.get(`/api/video/${id}`, { headers });
        console.log("받아온 비디오 데이터:", res.data);
        console.log("비디오 객체:", res.data.video);
        console.log("비디오 userId:", res.data.video?.userId);
        setVideo(res.data.video); // video 정보
        setTimeline(res.data.timeline || []); // 타임라인
        setComments(res.data.comments || []); // 댓글
        
        // 각 타임라인의 피드백 수와 목록을 API로 조회 (로그인 여부와 관계없이)
        const initialFeedbacks = {};
        const initialFeedbackLists = {};
        
        for (const item of (res.data.timeline || [])) {
          try {
            console.log(`타임라인 ${item.id}의 피드백 수 조회 중...`);
            // 피드백 수 조회 (JWT 토큰이 있으면 헤더 포함, 없으면 헤더 없이)
            const feedbackRes = await axios.get(`/api/timeline-feedback/timeline/${item.id}/count`, {
              headers: jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}
            });
            initialFeedbacks[item.id] = feedbackRes.data;
            console.log(`타임라인 ${item.id}: 피드백 수 ${feedbackRes.data}`);
            
            // 피드백 목록도 함께 조회 (JWT 토큰이 있으면 헤더 포함, 없으면 헤더 없이)
            const feedbackListRes = await axios.get(`/api/timeline-feedback/timeline/${item.id}`, {
              headers: jwtToken ? { Authorization: `Bearer ${jwtToken}` } : {}
            });
            initialFeedbackLists[item.id] = feedbackListRes.data;
            console.log(`타임라인 ${item.id}: 피드백 목록`, feedbackListRes.data);
            
          } catch (err) {
            console.error(`피드백 수 조회 중 오류 (timeline ${item.id}):`, err);
            initialFeedbacks[item.id] = 0;
            initialFeedbackLists[item.id] = [];
          }
        }
        console.log("초기 피드백 상태:", initialFeedbacks);
        console.log("초기 피드백 목록:", initialFeedbackLists);
        setFeedbacks(initialFeedbacks);
        setFeedbackLists(initialFeedbackLists);
        
        // 비디오 통계 정보 가져오기
        if (jwtToken) {
          try {
            const user = JSON.parse(localStorage.getItem("user"));
            const statsRes = await axios.get(`/api/video/${id}/stats?userId=${user.id}`, {
              headers: { Authorization: `Bearer ${jwtToken}` }
            });
            
            setVideoStats(statsRes.data);
            
            // 조회수 증가 (중복 방지)
            try {
              await axios.post(`/api/video/${id}/view?userId=${user.id}`, {}, {
                headers: { Authorization: `Bearer ${jwtToken}` }
              });
            } catch (err) {
              console.error("조회수 증가 중 오류:", err);
            }
          } catch (err) {
            console.error("비디오 통계 정보를 불러오는 중 오류:", err);
          }
        } else {
          // 로그인하지 않은 사용자에게 기본 통계 정보 제공
          setVideoStats({
            views: 0,
            likes: 0,
            isLikedByUser: false
          });
        }
        
        // 조회수 증가는 실제 동영상 시청 시에만 증가해야 하므로 제거
        // 새로고침 시마다 증가하는 것을 방지
      } catch (err) {
        console.error("동영상 상세 정보를 불러오는 중 오류:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchVideoDetail();
  }, [id, jwtToken]);

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

  const handleLikeToggle = async () => {
    if (!jwtToken) {
      if (window.confirm("좋아요를 누르려면 로그인이 필요합니다. 구글 로그인을 진행하시겠습니까?")) {
        handleGoogleLogin();
      }
      return;
    }

    try {
      const user = JSON.parse(localStorage.getItem("user"));
      
      // 현재 좋아요 상태 확인
      const currentLikedState = videoStats.isLikedByUser;
      
      // 백엔드에 좋아요 요청
      const res = await axios.post(`/api/video/${id}/like?userId=${user.id}&isLike=${!currentLikedState}`, {}, {
        headers: { Authorization: `Bearer ${jwtToken}` }
      });
      
      // 서버에서 최신 상태를 다시 가져오기
      const statsRes = await axios.get(`/api/video/${id}/stats?userId=${user.id}`, {
        headers: { Authorization: `Bearer ${jwtToken}` }
      });
      
      setVideoStats(statsRes.data);
      
    } catch (err) {
      console.error("좋아요 토글 중 오류:", err);
    }
  };

  const handleAddComment = async () => {
    if (!newComment.trim()) return;

    // JWT 토큰이 없으면 로그인 필요 메시지 표시
    if (!jwtToken) {
      if (window.confirm("댓글을 작성하려면 로그인이 필요합니다. 구글 로그인을 진행하시겠습니까?")) {
        handleGoogleLogin();
      }
      return;
    }

    try {
      const user = JSON.parse(localStorage.getItem("user"));
      const res = await axios.post(
        `/api/video/${id}/comment`,
        { content: newComment, userId: user.id },
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
          },
        }
      );
      setComments([...comments, res.data]); // 댓글 추가
      setNewComment("");
    } catch (err) {
      console.error("댓글 추가 중 오류:", err);
      alert("댓글 추가 중 오류가 발생했습니다.");
    }
  };

  // 피드백 모달 열기
  const openFeedbackModal = (timelineItem) => {
    // JWT 토큰이 없으면 로그인 필요 메시지 표시
    if (!jwtToken) {
      if (window.confirm("피드백을 작성하려면 로그인이 필요합니다. 구글 로그인을 진행하시겠습니까?")) {
        handleGoogleLogin();
      }
      return;
    }

    setSelectedTimeline(timelineItem);
    setNewFeedback("");
    setShowFeedbackModal(true);
  };

  // 피드백 입력창 토글
  const toggleFeedbackInput = (timelineId) => {
    if (!jwtToken) {
      alert("피드백을 작성하려면 로그인이 필요합니다.");
      return;
    }
    
    setShowFeedbackInputs(prev => ({
      ...prev,
      [timelineId]: !prev[timelineId]
    }));
    
    // 입력창이 열릴 때 새 피드백 텍스트 초기화
    if (!showFeedbackInputs[timelineId]) {
      setNewFeedbackTexts(prev => ({
        ...prev,
        [timelineId]: ""
      }));
    }
  };

  // 타임라인에 직접 피드백 추가
  const handleAddFeedbackToTimeline = async (timelineId) => {
    const feedbackText = newFeedbackTexts[timelineId];
    if (!feedbackText || !feedbackText.trim()) return;

    try {
      const user = JSON.parse(localStorage.getItem("user"));
      console.log("타임라인에 피드백 추가 시작:", { timelineId, userId: user.id });
      
      const res = await axios.post(
        `/api/timeline-feedback`,
        {
          comment: feedbackText,
          timelineId: timelineId,
          userId: user.id
        },
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
          },
        }
      );
      
      console.log("피드백 추가 성공:", res.data);
      
      // 피드백 수와 목록 업데이트
      try {
        const feedbackRes = await axios.get(`/api/timeline-feedback/timeline/${timelineId}/count`);
        const feedbackListRes = await axios.get(`/api/timeline-feedback/timeline/${timelineId}`);
        
        setFeedbacks(prev => ({
          ...prev,
          [timelineId]: feedbackRes.data
        }));
        
        setFeedbackLists(prev => ({
          ...prev,
          [timelineId]: feedbackListRes.data
        }));
        
        // 입력창 닫기 및 텍스트 초기화
        setShowFeedbackInputs(prev => ({
          ...prev,
          [timelineId]: false
        }));
        
        setNewFeedbackTexts(prev => ({
          ...prev,
          [timelineId]: ""
        }));
        
        console.log("피드백 상태 업데이트 완료");
        
      } catch (err) {
        console.error("피드백 상태 업데이트 중 오류:", err);
      }
      
    } catch (err) {
      console.error("피드백 추가 중 오류:", err);
      alert("피드백 추가 중 오류가 발생했습니다.");
    }
  };

  // 모달 방식 피드백 추가 (기존 방식)
  const handleAddFeedback = async () => {
    if (!newFeedback.trim()) return;

    try {
      const user = JSON.parse(localStorage.getItem("user"));
      console.log("피드백 추가 시작:", { timelineId: selectedTimeline.id, userId: user.id });
      
      const res = await axios.post(
        `/api/timeline-feedback`,
        {
          comment: newFeedback,
          timelineId: selectedTimeline.id,
          userId: user.id
        },
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
          },
        }
      );
      
      console.log("피드백 추가 성공:", res.data);
      
      // 피드백 수와 목록 업데이트
      try {
        const feedbackRes = await axios.get(`/api/timeline-feedback/timeline/${selectedTimeline.id}/count`);
        const feedbackListRes = await axios.get(`/api/timeline-feedback/timeline/${selectedTimeline.id}`);
        
        setFeedbacks(prev => ({
          ...prev,
          [selectedTimeline.id]: feedbackRes.data
        }));
        
        setFeedbackLists(prev => ({
          ...prev,
          [selectedTimeline.id]: feedbackListRes.data
        }));
        
        setNewFeedback("");
        setShowFeedbackModal(false);
        
        console.log("피드백 상태 업데이트 완료");
        
      } catch (err) {
        console.error("피드백 상태 업데이트 중 오류:", err);
      }
      
    } catch (err) {
      console.error("피드백 추가 중 오류:", err);
      alert("피드백 추가 중 오류가 발생했습니다.");
    }
  };

  // 피드백 수정 시작
  const startEditFeedback = (feedbackId, currentText) => {
    setEditingFeedbacks(prev => ({
      ...prev,
      [feedbackId]: true
    }));
    setEditFeedbackTexts(prev => ({
      ...prev,
      [feedbackId]: currentText
    }));
  };

  // 피드백 수정 취소
  const cancelEditFeedback = (feedbackId) => {
    setEditingFeedbacks(prev => ({
      ...prev,
      [feedbackId]: false
    }));
    setEditFeedbackTexts(prev => ({
      ...prev,
      [feedbackId]: ""
    }));
  };

  // 피드백 수정 저장
  const saveEditFeedback = async (feedbackId, timelineId) => {
    const editText = editFeedbackTexts[feedbackId];
    if (!editText || !editText.trim()) return;

    try {
      const user = JSON.parse(localStorage.getItem("user"));
      console.log("피드백 수정 시작:", { feedbackId, timelineId, userId: user.id });
      
      const res = await axios.put(
        `/api/timeline-feedback/${feedbackId}`,
        {
          comment: editText,
          userId: user.id
        },
        {
          headers: {
            Authorization: `Bearer ${jwtToken}`,
          },
        }
      );
      
      console.log("피드백 수정 성공:", res.data);
      
      // 피드백 목록 업데이트
      try {
        const feedbackListRes = await axios.get(`/api/timeline-feedback/timeline/${timelineId}`, {
          headers: { Authorization: `Bearer ${jwtToken}` }
        });
        
        setFeedbackLists(prev => ({
          ...prev,
          [timelineId]: feedbackListRes.data
        }));
        
        // 수정 모드 종료
        setEditingFeedbacks(prev => ({
          ...prev,
          [feedbackId]: false
        }));
        
        console.log("피드백 수정 완료");
        
      } catch (err) {
        console.error("피드백 목록 업데이트 중 오류:", err);
      }
      
    } catch (err) {
      console.error("피드백 수정 중 오류:", err);
      alert("피드백 수정 중 오류가 발생했습니다.");
    }
  };

  // 피드백 삭제
  const deleteFeedback = async (feedbackId, timelineId) => {
    if (!window.confirm("정말로 이 피드백을 삭제하시겠습니까?")) return;

    try {
      const user = JSON.parse(localStorage.getItem("user"));
      console.log("피드백 삭제 시작:", { feedbackId, timelineId, userId: user.id });
      
      await axios.delete(`/api/timeline-feedback/${feedbackId}?userId=${user.id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });
      
      console.log("피드백 삭제 성공");
      
      // 피드백 수와 목록 업데이트
      try {
        const feedbackRes = await axios.get(`/api/timeline-feedback/timeline/${timelineId}/count`, {
          headers: { Authorization: `Bearer ${jwtToken}` }
        });
        const feedbackListRes = await axios.get(`/api/timeline-feedback/timeline/${timelineId}`, {
          headers: { Authorization: `Bearer ${jwtToken}` }
        });
        
        setFeedbacks(prev => ({
          ...prev,
          [timelineId]: feedbackRes.data
        }));
        
        setFeedbackLists(prev => ({
          ...prev,
          [timelineId]: feedbackListRes.data
        }));
        
        console.log("피드백 삭제 완료");
        
      } catch (err) {
        console.error("피드백 상태 업데이트 중 오류:", err);
      }
      
    } catch (err) {
      console.error("피드백 삭제 중 오류:", err);
      alert("피드백 삭제 중 오류가 발생했습니다.");
    }
  };

  // 비디오 삭제 함수
  const handleDeleteVideo = async () => {
    if (!window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) return;

    try {
      const user = JSON.parse(localStorage.getItem("user"));
      console.log("비디오 삭제 시작:", { videoId: id, userId: user.id });
      
      await axios.delete(`/api/video/${id}?userId=${user.id}`, {
        headers: {
          Authorization: `Bearer ${jwtToken}`,
        },
      });
      
      console.log("비디오 삭제 성공");
      alert("게시글이 삭제되었습니다.");
      window.location.href = "/videos"; // 동영상 리스트로 리다이렉트
      
    } catch (err) {
      console.error("비디오 삭제 중 오류:", err);
      alert("게시글 삭제 중 오류가 발생했습니다.");
    }
  };

  if (loading) {
    return <Container className="mt-4">로딩 중...</Container>;
  }

  if (!video) {
    return <Container className="mt-4">동영상을 찾을 수 없습니다.</Container>;
  }

  // 시간을 mm:ss 형태로 변환하는 함수
  const formatTime = (seconds) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m.toString().padStart(2, "0")}:${s.toString().padStart(2, "0")}`;
  };

  const seekTo = (seconds) => {
    if (iframeRef.current) {
      iframeRef.current.contentWindow.postMessage(
        JSON.stringify({ event: "command", func: "seekTo", args: [seconds, true] }),
        "*"
      );
    }
  };

  return (
    <Container className="video-detail-container mt-4">
      {/* 동영상 */}
      <Card className="video-player-card mb-4">
        <div className="ratio ratio-16x9">
          <iframe
            ref={iframeRef}
            id="ytplayer"
            src={`https://www.youtube.com/embed/${video.youtubeVideoId}?enablejsapi=1`}
            title={video.title}
            allowFullScreen
          ></iframe>
        </div>
      </Card>

      {/* 비디오 정보 */}
      <Card className="video-info-card mb-4 p-3">
        <div className="d-flex justify-content-between align-items-start">
          <div>
            <h3 className="video-title mb-2">{video.title}</h3>
            <p className="video-description text-muted mb-3">{video.description}</p>
            <div className="video-uploader mb-2">
              {video.summonerName ? (
                <>
                  <span className="uploader-summoner text-primary fw-bold">{video.summonerName}</span>
                  {video.tier && video.tier !== 'UNRANKED' && (
                    <span className="uploader-tier ms-2">
                      {getTierDisplayName(video.tier)}
                    </span>
                  )}
                </>
              ) : (
                <span className="uploader-anonymous text-muted">익명</span>
              )}
            </div>
          </div>
          <div className="video-stats d-flex flex-column align-items-end gap-2">
            <div className="d-flex align-items-center gap-3">
              <div className="stat-item d-flex align-items-center gap-2">
                <i className="fas fa-eye text-muted"></i>
                <span className="stat-value">{videoStats.views}</span>
                <span className="stat-label">조회수</span>
              </div>
              <div className="stat-item d-flex align-items-center gap-2">
                <i className="fas fa-heart text-muted"></i>
                <span className="stat-value">{videoStats.likes}</span>
                <span className="stat-label">좋아요</span>
              </div>
            </div>
            <div className="d-flex gap-2">
              {jwtToken && (
                <Button
                  variant={videoStats.isLikedByUser ? "danger" : "outline-danger"}
                  size="sm"
                  onClick={handleLikeToggle}
                  className="like-btn"
                >
                  <i className={`fas fa-heart ${videoStats.isLikedByUser ? 'text-white' : 'text-danger'}`}></i>
                  {videoStats.isLikedByUser ? ' 좋아요 취소' : ' 좋아요'}
                </Button>
              )}
              {/* 게시글 삭제 버튼 - 작성자만 표시 */}
              {(() => {
                console.log('삭제 버튼 렌더링 조건 확인:', {
                  jwtToken: !!jwtToken,
                  video: video,
                  videoUserId: video?.userId,
                  videoKeys: video ? Object.keys(video) : 'video is null'
                });
                
                if (!jwtToken) {
                  console.log('JWT 토큰 없음');
                  return false;
                }
                
                if (!video?.userId) {
                  console.log('비디오 userId 없음');
                  return false;
                }
                
                const currentUser = JSON.parse(localStorage.getItem("user"));
                const isOwner = currentUser && currentUser.id === video.userId;
                
                console.log('삭제 버튼 권한 확인:', {
                  currentUser: currentUser,
                  currentUserId: currentUser?.id,
                  isOwner: isOwner
                });
                
                return isOwner;
              })() && (
                <Button
                  variant="outline-danger"
                  size="sm"
                  onClick={handleDeleteVideo}
                  className="delete-video-btn"
                >
                  <i className="fas fa-trash text-danger me-1"></i>
                  삭제
                </Button>
              )}
            </div>
          </div>
        </div>
      </Card>

      {/* 타임라인 */}
      <Card className="timeline-section mb-4 p-3">
        <h4>타임라인</h4>
        {timeline.length === 0 && <p>타임라인이 없습니다.</p>}
        {timeline.map((item, index) => (
          <div key={index} className="timeline-item mb-3 p-3 border rounded">
            <div className="d-flex justify-content-between align-items-start mb-2">
              <div>
                <span
                  className="timeline-time"
                  onClick={() => seekTo(item.startTime)}
                >
                  {formatTime(item.startTime)}
                </span>
                -
                <span
                  className="timeline-time"
                  onClick={() => seekTo(item.endTime)}
                >
                  {formatTime(item.endTime)}
                </span>
              </div>
              <div className="d-flex align-items-center gap-2">
                <Badge bg="secondary" className="feedback-count">
                  피드백 {feedbacks[item.id] || 0}
                </Badge>
              </div>
            </div>
            <div className="timeline-content">
              {item.content}
            </div>
            
            {/* 피드백 목록 (JWT 토큰이 있을 때만 표시) */}
            {jwtToken && (
              <div className="feedback-list mt-3">
                <h6 className="text-muted mb-2">
                </h6>
                
                {feedbackLists[item.id] && feedbackLists[item.id].length > 0 ? (
                  feedbackLists[item.id].map((feedback) => (
                    <div key={feedback.id} className="video-feedback-item p-2 mb-2 bg-light rounded">
                      <div className="d-flex justify-content-between align-items-start">
                        <div className="d-flex align-items-center gap-2">
                          {feedback.summonerName ? (
                            <strong className="text-primary">{feedback.summonerName}</strong>
                          ) : (
                            <strong className="text-primary">{feedback.userName}</strong>
                          )}
                          {feedback.tier && feedback.tier !== 'UNRANKED' && (
                            <span className="video-tier-badge">
                              {getTierDisplayName(feedback.tier)}
                            </span>
                          )}
                          <span className="text-muted ms-2">
                            {new Date(feedback.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                      </div>
                      <div className="video-feedback-comment mt-1">
                        {editingFeedbacks[feedback.id] ? (
                          <Form.Control
                            as="textarea"
                            rows={2}
                            value={editFeedbackTexts[feedback.id] || ""}
                            onChange={(e) => setEditFeedbackTexts(prev => ({
                              ...prev,
                              [feedback.id]: e.target.value
                            }))}
                          />
                        ) : (
                          feedback.comment
                        )}
                      </div>
                      {editingFeedbacks[feedback.id] ? (
                        <div className="d-flex gap-2 mt-2">
                          <Button
                            variant="success"
                            size="sm"
                            onClick={() => saveEditFeedback(feedback.id, item.id)}
                          >
                            저장
                          </Button>
                          <Button
                            variant="outline-secondary"
                            size="sm"
                            onClick={() => cancelEditFeedback(feedback.id)}
                          >
                            취소
                          </Button>
                        </div>
                      ) : (
                        <div className="d-flex gap-2 mt-2">
                          <Button
                            variant="outline-primary"
                            size="sm"
                            onClick={() => startEditFeedback(feedback.id, feedback.comment)}
                          >
                            수정
                          </Button>
                          <Button
                            variant="outline-danger"
                            size="sm"
                            onClick={() => deleteFeedback(feedback.id, item.id)}
                          >
                            삭제
                          </Button>
                        </div>
                      )}
                    </div>
                  ))
                ) : (
                  <div className="text-muted text-center py-2">
                    아직 피드백이 없습니다.
                  </div>
                )}
              </div>
            )}
            
            {/* 피드백 입력창 (JWT 토큰이 있을 때만 표시) */}
            {jwtToken && showFeedbackInputs[item.id] && (
              <div className="video-feedback-input-section mt-3 p-3 bg-light rounded">
                <Form.Group>
                  <Form.Label className="text-muted">새 피드백 작성</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={2}
                    placeholder="이 타임라인에 대한 피드백을 입력하세요"
                    value={newFeedbackTexts[item.id] || ""}
                    onChange={(e) => setNewFeedbackTexts(prev => ({
                      ...prev,
                      [item.id]: e.target.value
                    }))}
                  />
                </Form.Group>
                <div className="d-flex gap-2 mt-2">
                  <Button
                    variant="primary"
                    size="sm"
                    onClick={() => handleAddFeedbackToTimeline(item.id)}
                  >
                    등록
                  </Button>
                  <Button
                    variant="outline-secondary"
                    size="sm"
                    onClick={() => toggleFeedbackInput(item.id)}
                  >
                    취소
                  </Button>
                </div>
              </div>
            )}
            
            {/* 피드백 추가 버튼 (JWT 토큰이 있을 때만 표시) */}
            {jwtToken && (
              <div className="mt-2">
                <Button
                  onClick={() => toggleFeedbackInput(item.id)}
                  className="video-feedback-add-btn"
                >
                  {showFeedbackInputs[item.id] ? "피드백 작성 취소" : "피드백 추가"}
                </Button>
              </div>
            )}
          </div>
        ))}
      </Card>

      {/* 댓글 */}
      <Card className="video-comments-section mb-4 p-3">
        <h4>댓글</h4>
        {comments.length === 0 && <p>댓글이 없습니다.</p>}
        {comments.map((comment) => (
          <Card key={comment.id} className="video-comment-item mb-2 p-2">
            <strong>{comment.userName}</strong>: {comment.content}
          </Card>
        ))}

        {/* 로그인한 사용자만 댓글 작성 폼 표시 */}
        {jwtToken ? (
          <Form className="video-comment-form mt-3 d-flex align-items-center">
            <Form.Control
              type="text"
              placeholder="댓글을 입력하세요"
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              className="video-comment-input"
            />
            <Button
              variant="primary"
              onClick={handleAddComment}
              className="video-comment-button"
            >
              등록
            </Button>
          </Form>
        ) : (
          <div className="text-center mt-3 p-3 bg-light rounded">
            <p className="text-muted mb-0">댓글을 작성하려면 로그인이 필요합니다.</p>
          </div>
        )}
      </Card>

      {/* 피드백 모달 */}
      <Modal 
        show={showFeedbackModal} 
        onHide={() => setShowFeedbackModal(false)}
        className="video-feedback-modal"
      >
        <Modal.Header closeButton>
          <Modal.Title>타임라인 피드백</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="mb-3">
            <strong>타임라인:</strong> {selectedTimeline && (
              <>
                {formatTime(selectedTimeline.startTime)} - {formatTime(selectedTimeline.endTime)}
                <br />
                <span className="text-muted">{selectedTimeline.content}</span>
              </>
            )}
          </div>
          <Form>
            <Form.Group>
              <Form.Label>피드백 내용</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                placeholder="이 타임라인에 대한 피드백을 입력하세요"
                value={newFeedback}
                onChange={(e) => setNewFeedback(e.target.value)}
              />
            </Form.Group>
          </Form>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowFeedbackModal(false)}>
            취소
          </Button>
          <Button variant="primary" onClick={handleAddFeedback}>
            피드백 등록
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}

export default VideoDetail;
