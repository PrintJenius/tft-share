package com.neojen.tft_share.controller;

import com.neojen.tft_share.dto.TimelineFeedbackDto;
import com.neojen.tft_share.service.TimelineFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline-feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TimelineFeedbackController {

    private final TimelineFeedbackService feedbackService;

    // 피드백 생성
    @PostMapping
    public ResponseEntity<TimelineFeedbackDto> createFeedback(@RequestBody TimelineFeedbackDto feedbackDto) {
        TimelineFeedbackDto createdFeedback = feedbackService.createFeedback(feedbackDto);
        return ResponseEntity.ok(createdFeedback);
    }

    // 피드백 수정
    @PutMapping("/{feedbackId}")
    public ResponseEntity<TimelineFeedbackDto> updateFeedback(
            @PathVariable Long feedbackId,
            @RequestBody TimelineFeedbackDto feedbackDto) {
        TimelineFeedbackDto updatedFeedback = feedbackService.updateFeedback(feedbackId, feedbackDto);
        return ResponseEntity.ok(updatedFeedback);
    }

    // 타임라인별 피드백 목록 조회
    @GetMapping("/timeline/{timelineId}")
    public ResponseEntity<List<TimelineFeedbackDto>> getFeedbacksByTimelineId(@PathVariable Long timelineId) {
        List<TimelineFeedbackDto> feedbacks = feedbackService.getFeedbacksByTimelineId(timelineId);
        return ResponseEntity.ok(feedbacks);
    }

    // 타임라인별 피드백 수 조회
    @GetMapping("/timeline/{timelineId}/count")
    public ResponseEntity<Long> getFeedbackCountByTimelineId(@PathVariable Long timelineId) {
        Long count = feedbackService.getFeedbackCountByTimelineId(timelineId);
        return ResponseEntity.ok(count);
    }

    // 피드백 삭제
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(
            @PathVariable Long feedbackId,
            @RequestParam Long userId) {
        feedbackService.deleteFeedback(feedbackId, userId);
        return ResponseEntity.ok().build();
    }
}
