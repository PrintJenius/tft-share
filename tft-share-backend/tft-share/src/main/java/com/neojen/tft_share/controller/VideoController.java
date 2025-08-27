package com.neojen.tft_share.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

import com.neojen.tft_share.dto.VideoListDto;
import com.neojen.tft_share.dto.VideoCommentDto;
import com.neojen.tft_share.entity.Video;
import com.neojen.tft_share.entity.VideoComment;
import com.neojen.tft_share.entity.VideoTimeline;
import com.neojen.tft_share.service.VideoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {

	private final VideoService videoService;

    // 동영상 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<VideoListDto>> getVideoList() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }
    
    // 페이징과 정렬을 지원하는 동영상 목록 조회
    @GetMapping("/list/paged")
    public ResponseEntity<?> getVideoListPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        return ResponseEntity.ok(videoService.getVideosWithPaging(page, size));
    }
    
    // 특정 동영상 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoDetail(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getVideoDetail(id));
    }
    
    // 조회수 증가
    @PostMapping("/{id}/view")
    public ResponseEntity<?> incrementViews(@PathVariable Long id, @RequestParam Long userId) {
        boolean incremented = videoService.incrementViews(id, userId);
        if (incremented) {
            return ResponseEntity.ok(Map.of("message", "조회수가 증가되었습니다."));
        } else {
            return ResponseEntity.ok(Map.of("message", "아직 조회수 증가 쿨다운 시간이 지나지 않았습니다."));
        }
    }
    
    // 좋아요 토글
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, @RequestParam Long userId, @RequestParam boolean isLike) {
        boolean result = videoService.toggleLike(id, userId, isLike);
        return ResponseEntity.ok(Map.of("isLiked", result));
    }
    
    // 비디오 통계 정보 조회
    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getVideoStats(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(videoService.getVideoStats(id, userId));
    }
    
    // 댓글 추가
    @PostMapping("/{id}/comment")
    public ResponseEntity<?> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String content = (String) request.get("content");
            Long userId = Long.valueOf(request.get("userId").toString());
            
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("댓글 내용을 입력해주세요.");
            }
            
            VideoCommentDto comment = videoService.addComment(id, userId, content);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("댓글 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 비디오 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id, @RequestParam Long userId) {
        try {
            videoService.deleteVideo(id, userId);
            return ResponseEntity.ok(Map.of("message", "비디오가 삭제되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("비디오 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
