package com.neojen.tft_share.service;

import com.neojen.tft_share.dto.TimelineFeedbackDto;
import com.neojen.tft_share.entity.TimelineFeedback;
import com.neojen.tft_share.entity.User;
import com.neojen.tft_share.entity.VideoTimeline;
import com.neojen.tft_share.repository.TimelineFeedbackRepository;
import com.neojen.tft_share.repository.UserRepository;
import com.neojen.tft_share.repository.VideoTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimelineFeedbackService {

    private final TimelineFeedbackRepository feedbackRepository;
    private final VideoTimelineRepository timelineRepository;
    private final UserRepository userRepository;

    // 피드백 생성
    @Transactional
    public TimelineFeedbackDto createFeedback(TimelineFeedbackDto feedbackDto) {
        VideoTimeline timeline = timelineRepository.findById(feedbackDto.getTimelineId())
                .orElseThrow(() -> new RuntimeException("Timeline not found"));
        
        User user = userRepository.findById(feedbackDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        TimelineFeedback feedback = TimelineFeedback.builder()
                .comment(feedbackDto.getComment())
                .timeline(timeline)
                .user(user)
                .build();

        TimelineFeedback savedFeedback = feedbackRepository.save(feedback);
        return convertToDto(savedFeedback);
    }

    // 피드백 수정
    @Transactional
    public TimelineFeedbackDto updateFeedback(Long feedbackId, TimelineFeedbackDto feedbackDto) {
        TimelineFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        // 작성자 본인만 수정 가능
        if (!feedback.getUser().getId().equals(feedbackDto.getUserId())) {
            throw new RuntimeException("Unauthorized to update this feedback");
        }
        
        feedback.setComment(feedbackDto.getComment());
        TimelineFeedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToDto(updatedFeedback);
    }

    // 타임라인별 피드백 목록 조회
    public List<TimelineFeedbackDto> getFeedbacksByTimelineId(Long timelineId) {
        List<TimelineFeedback> feedbacks = feedbackRepository.findByTimelineIdOrderByCreatedAtDesc(timelineId);
        return feedbacks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 타임라인별 피드백 수 조회
    public Long getFeedbackCountByTimelineId(Long timelineId) {
        return feedbackRepository.countByTimelineId(timelineId);
    }

    // 피드백 삭제
    @Transactional
    public void deleteFeedback(Long feedbackId, Long userId) {
        TimelineFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        if (!feedback.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this feedback");
        }
        
        feedbackRepository.delete(feedback);
    }

    // DTO 변환
    private TimelineFeedbackDto convertToDto(TimelineFeedback feedback) {
        User user = feedback.getUser();
        return TimelineFeedbackDto.builder()
                .id(feedback.getId())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .timelineId(feedback.getTimeline().getId())
                .userId(user.getId())
                .userName(user.getName())
                .summonerName(user.getSummonerName())
                .tier(user.getTier() != null ? user.getTier().name() : "UNRANKED")
                .build();
    }
}
