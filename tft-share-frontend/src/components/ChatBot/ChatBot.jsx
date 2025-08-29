import React, { useState } from 'react';
import { Button, Card, Badge } from 'react-bootstrap';
import { FaComment, FaTimes, FaArrowLeft } from 'react-icons/fa';
import './ChatBot.css';

function ChatBot() {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedQuestion, setSelectedQuestion] = useState(null);

  // FAQ 카테고리 및 질문/답변 데이터
  const faqData = {
    '서비스 소개': [
      {
        question: 'TFT Share는 무엇인가요?',
        answer: 'TFT Share는 TFT 게임 플레이를 공유하고 피드백을 받을 수 있는 플랫폼입니다.'
      },
      {
        question: '어떤 기능들이 있나요?',
        answer: '동영상 업로드, 타임라인 피드백, 사용자 프로필 관리 등의 기능을 제공합니다.'
      }
    ],
    '사용법 가이드': [
      {
        question: '동영상을 어떻게 업로드하나요?',
        answer: 'Google 로그인 후 Upload 메뉴에서 동영상 파일을 선택하고 제목과 설명을 입력하면 됩니다.'
      },
      {
        question: '피드백을 어떻게 받나요?',
        answer: '동영상의 특정 시간대에 피드백을 요청할 수 있고, 다른 사용자들이 코멘트를 남겨줍니다.'
      }
    ],
    '기술 정보': [
      {
        question: '어떤 기술로 만들어졌나요?',
        answer: 'Spring Boot 백엔드와 React 프론트엔드로 구성되어 있습니다.'
      },
      {
        question: '데이터는 어디에 저장되나요?',
        answer: 'AWS RDS MySQL과 S3를 사용하여 데이터를 안전하게 저장합니다.'
      }
    ]
  };

  const toggleChat = () => {
    setIsOpen(!isOpen);
    if (!isOpen) {
      setSelectedCategory(null);
      setSelectedQuestion(null);
    }
  };

  const handleCategoryClick = (category) => {
    setSelectedCategory(category);
    setSelectedQuestion(null);
  };

  const handleQuestionClick = (question) => {
    setSelectedQuestion(question);
  };

  const handleBackToCategories = () => {
    setSelectedCategory(null);
    setSelectedQuestion(null);
  };

  const handleBackToQuestions = () => {
    setSelectedQuestion(null);
  };

  return (
    <div className="chatbot-container">
      {/* 챗봇 토글 버튼 */}
      <Button
        className={`chatbot-toggle ${isOpen ? 'active' : ''}`}
        onClick={toggleChat}
        variant="primary"
        size="lg"
      >
        {isOpen ? <FaTimes /> : <FaComment />}
      </Button>

      {/* 챗봇 패널 */}
      {isOpen && (
        <Card className="chatbot-panel">
          <Card.Header className="chatbot-header">
            <div className="d-flex align-items-center">
                             <FaComment className="me-2" />
               <span className="fw-bold">TFT Share 도우미</span>
            </div>
            <Badge bg="success">온라인</Badge>
          </Card.Header>
          
          <Card.Body className="chatbot-body">
            {!selectedCategory && !selectedQuestion && (
              <div className="welcome-message">
                <h6>안녕하세요! 무엇을 도와드릴까요?</h6>
                <p className="text-muted">아래 카테고리 중에서 선택해주세요.</p>
                
                <div className="category-list">
                  {Object.keys(faqData).map((category) => (
                    <Button
                      key={category}
                      variant="outline-primary"
                      className="category-btn"
                      onClick={() => handleCategoryClick(category)}
                    >
                      {category}
                    </Button>
                  ))}
                </div>
              </div>
            )}

            {selectedCategory && !selectedQuestion && (
                             <div className="question-list">
                 <div className="d-flex align-items-center justify-content-between mb-3">
                   <h6 className="mb-0">{selectedCategory}</h6>
                   <Button
                     variant="link"
                     className="p-0"
                     onClick={handleBackToCategories}
                   >
                     <FaArrowLeft />
                   </Button>
                 </div>
                
                <div className="question-items">
                  {faqData[selectedCategory].map((item, index) => (
                    <Button
                      key={index}
                      variant="light"
                      className="question-btn"
                      onClick={() => handleQuestionClick(item)}
                    >
                      {item.question}
                    </Button>
                  ))}
                </div>
              </div>
            )}

            {selectedQuestion && (
                             <div className="answer-view">
                 <div className="d-flex align-items-center justify-content-between mb-3">
                   <h6 className="mb-0">질문</h6>
                   <Button
                     variant="link"
                     className="p-0"
                     onClick={handleBackToQuestions}
                   >
                     <FaArrowLeft />
                   </Button>
                 </div>
                
                <div className="question-text mb-3">
                  <strong>{selectedQuestion.question}</strong>
                </div>
                
                                 <div className="answer-text">
                   <p>{selectedQuestion.answer}</p>
                 </div>
              </div>
            )}
          </Card.Body>
        </Card>
      )}
    </div>
  );
}

export default ChatBot;
