package com.neojen.tft_share.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import com.neojen.tft_share.dto.VideoListDto;
import com.neojen.tft_share.dto.VideoDetailDto;
import com.neojen.tft_share.dto.VideoCommentDto;
import com.neojen.tft_share.dto.VideoTimelineDto;
import com.neojen.tft_share.entity.User;
import com.neojen.tft_share.entity.Video;
import com.neojen.tft_share.entity.VideoComment;
import com.neojen.tft_share.entity.VideoTimeline;
import com.neojen.tft_share.entity.VideoLikes;
import com.neojen.tft_share.repository.VideoCommentRepository;
import com.neojen.tft_share.repository.VideoRepository;
import com.neojen.tft_share.repository.VideoTimelineRepository;
import com.neojen.tft_share.repository.VideoLikesRepository;
import com.neojen.tft_share.repository.TimelineFeedbackRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService { 

    private final UserService userService;
    private final GoogleTokenRefreshService tokenRefreshService;
    private final VideoRepository videoRepository;
    private final VideoTimelineRepository timelineRepository;
    private final VideoCommentRepository videoCommentRepository;
    private final VideoLikesRepository likesRepository;
    private final TimelineFeedbackRepository timelineFeedbackRepository;
    
    // 조회수 중복 증가 방지를 위한 메모리 캐시 (사용자ID_비디오ID -> 마지막 조회 시간)
    private final ConcurrentHashMap<String, Long> viewCache = new ConcurrentHashMap<>();
    private static final long VIEW_COOLDOWN_MS = 60 * 60 * 1000; // 1시간 (밀리초)

    @Transactional
    public com.neojen.tft_share.entity.Video uploadVideo(MultipartFile file, String title, String description,
                             String timelineJson, Long userId) throws Exception {

        // 1. 사용자 정보 조회
    	User user = userService.findEntityById(userId);
        if (user == null) {
            throw new IllegalArgumentException("유효한 유저가 없습니다.");
        }
        
        // 2. 유효한 Access Token 가져오기 (만료된 경우 자동 갱신)
        String accessToken = tokenRefreshService.getValidAccessToken(user);

        // 3. YouTube 서비스 객체 생성
        YouTube youtube = new YouTube.Builder(
                new com.google.api.client.http.javanet.NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + accessToken)
        ).setApplicationName("TFT Share").build();

        // 4. 유튜브 동영상 메타 정보
        com.google.api.services.youtube.model.Video videoMeta = 
    	    new com.google.api.services.youtube.model.Video();
        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(title);
        snippet.setDescription(description);
        snippet.setTags(Collections.singletonList("TFT"));
        videoMeta.setSnippet(snippet);
        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus("public");
        videoMeta.setStatus(status);

        // 5. 업로드
        InputStreamContent mediaContent = new InputStreamContent(
                file.getContentType(),
                file.getInputStream()
        );
        
        com.google.api.services.youtube.model.Video response;
        try {
            response = youtube.videos()
                    .insert("snippet,status", videoMeta, mediaContent)
                    .execute();
        } catch (com.google.api.client.googleapis.json.GoogleJsonResponseException e) {
            if (e.getStatusCode() == 401) {
                throw new Exception("Google 인증이 만료되었습니다. 다시 로그인해주세요.");
            } else {
                throw new Exception("YouTube 업로드 중 오류 발생: " + e.getMessage());
            }
        }
        
        String youtubeVideoId = response.getId();
        
        String thumbnailUrl = "https://img.youtube.com/vi/" + youtubeVideoId + "/hqdefault.jpg";

        // 6. DB에 Video 저장
        com.neojen.tft_share.entity.Video video = com.neojen.tft_share.entity.Video.builder()
                .title(title)
                .description(description)
                .youtubeVideoId(youtubeVideoId)
                .thumbnailUrl(thumbnailUrl)
                .user(user)
                .build();
        videoRepository.save(video);

        // 7. 타임라인 JSON 파싱 후 DB 저장
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> timelineList = objectMapper.readValue(timelineJson, List.class);
        for (Map<String, Object> item : timelineList) {
            VideoTimeline timeline = new VideoTimeline();
            timeline.setVideo(video);
            timeline.setStartTime((Integer) item.get("start_time"));
            timeline.setEndTime((Integer) item.get("end_time"));
            timeline.setContent((String) item.get("content"));
            timelineRepository.save(timeline);
        }

        return video;
    }
    
    public List<VideoListDto> getAllVideos() {
        return videoRepository.findAll()
                .stream()
                .map(v -> new VideoListDto(
                        v.getId(),
                        v.getTitle(),
                        v.getYoutubeVideoId(),
                        v.getThumbnailUrl(),
                        v.getViews() != null ? v.getViews() : 0,
                        (int) likesRepository.countByVideo(v),
                        (int) videoCommentRepository.countByVideoId(v.getId()),
                        v.getCreatedAt(),
                        v.getUser().getSummonerName(),
                        v.getUser().getTier() != null ? v.getUser().getTier().name() : "UNRANKED"
                ))
                .collect(Collectors.toList());
    }
    
    // 페이징과 정렬을 지원하는 비디오 목록 조회
    public Map<String, Object> getVideosWithPaging(int page, int size) {
        // 최신순으로 정렬 (createdAt 기준 내림차순)
        List<Video> allVideos = videoRepository.findAllByOrderByCreatedAtDesc();
        
        int totalVideos = allVideos.size();
        int totalPages = (int) Math.ceil((double) totalVideos / size);
        
        // 페이징 처리
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalVideos);
        
        List<VideoListDto> pagedVideos = allVideos.subList(startIndex, endIndex)
                .stream()
                .map(v -> new VideoListDto(
                        v.getId(),
                        v.getTitle(),
                        v.getYoutubeVideoId(),
                        v.getThumbnailUrl(),
                        v.getViews() != null ? v.getViews() : 0,
                        (int) likesRepository.countByVideo(v),
                        (int) videoCommentRepository.countByVideoId(v.getId()),
                        v.getCreatedAt(),
                        v.getUser().getSummonerName(),
                        v.getUser().getTier() != null ? v.getUser().getTier().name() : "UNRANKED"
                ))
                .collect(Collectors.toList());
        
        return Map.of(
                "videos", pagedVideos,
                "currentPage", page,
                "totalPages", totalPages,
                "totalVideos", totalVideos,
                "hasNext", page < totalPages - 1,
                "hasPrevious", page > 0
        );
    }

    public Map<String, Object> getVideoDetail(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("동영상을 찾을 수 없습니다. id=" + videoId));

        // 서비스 내부에서 timeline과 comment 조회
        List<VideoTimeline> timelineList = timelineRepository.findByVideoId(videoId);
        List<VideoComment> commentList = videoCommentRepository.findByVideoId(videoId);

        // 댓글을 DTO로 변환
        List<VideoCommentDto> commentDtoList = commentList.stream()
                .map(comment -> new VideoCommentDto(
                        comment.getId(),
                        comment.getContent(),
                        comment.getUser().getName(),
                        comment.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());

        // VideoDetailDto 생성
        VideoDetailDto videoDetailDto = new VideoDetailDto(
                video.getId(),
                video.getUser().getId(),  // userId 추가
                video.getTitle(),
                video.getDescription(),
                video.getYoutubeVideoId(),
                video.getThumbnailUrl(),
                video.getUser().getSummonerName(),
                video.getUser().getTier() != null ? video.getUser().getTier().name() : "UNRANKED",
                timelineList.stream().map(timeline -> new VideoTimelineDto(
                        timeline.getId(),
                        timeline.getStartTime(),
                        timeline.getEndTime(),
                        timeline.getContent()
                )).collect(Collectors.toList()),
                commentDtoList
        );

        // 하나의 Map으로 합쳐서 반환
        return Map.of(
                "video", videoDetailDto,
                "timeline", timelineList,
                "comments", commentDtoList
        );
    }
    
    // 조회수 증가 (중복 방지)
    @Transactional
    public boolean incrementViews(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("동영상을 찾을 수 없습니다. id=" + videoId));
        
        // 캐시 키 생성
        String cacheKey = userId + "_" + videoId;
        long currentTime = System.currentTimeMillis();
        
        // 마지막 조회 시간 확인
        Long lastViewTime = viewCache.get(cacheKey);
        if (lastViewTime != null && (currentTime - lastViewTime) < VIEW_COOLDOWN_MS) {
            // 아직 쿨다운 시간이 지나지 않음
            return false;
        }
        
        // views가 null일 경우 0으로 초기화
        Integer currentViews = video.getViews();
        if (currentViews == null) {
            currentViews = 0;
        }
        
        // 조회수 증가 및 캐시 업데이트
        video.setViews(currentViews + 1);
        videoRepository.save(video);
        viewCache.put(cacheKey, currentTime);
        
        return true;
    }
    
    // 좋아요 토글 (좋아요가 없으면 추가, 있으면 제거)
    @Transactional
    public boolean toggleLike(Long videoId, Long userId, boolean isLike) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("동영상을 찾을 수 없습니다. id=" + videoId));
        
        User user = userService.findEntityById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        
        if (isLike) {
            // 좋아요 추가
            if (!likesRepository.existsByUserAndVideo(user, video)) {
                VideoLikes videoLike = new VideoLikes();
                videoLike.setUser(user);
                videoLike.setVideo(video);
                likesRepository.save(videoLike);
            }
        } else {
            // 좋아요 제거
            if (user.getId() != null && video.getId() != null) {
                likesRepository.deleteByUserAndVideo(user, video);
            } else {
                System.err.println("User or Video ID is null, cannot delete like.");
            }
        }
        
        return isLike;
    }
    
    // 비디오 통계 정보 조회
    public Map<String, Object> getVideoStats(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("비디오를 찾을 수 없습니다."));
        
        User user = userService.findEntityById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        
        int views = video.getViews() != null ? video.getViews() : 0;
        int likes = (int) likesRepository.countByVideo(video);
        boolean isLikedByUser = likesRepository.existsByUserAndVideo(user, video);
        
        return Map.of(
            "views", views,
            "likes", likes,
            "isLikedByUser", isLikedByUser
        );
    }
    
    // 댓글 추가
    @Transactional
    public VideoCommentDto addComment(Long videoId, Long userId, String content) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("비디오를 찾을 수 없습니다."));
        
        User user = userService.findEntityById(userId);
        if (user == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        
        VideoComment comment = new VideoComment();
        comment.setContent(content);
        comment.setVideo(video);
        comment.setUser(user);
        
        VideoComment savedComment = videoCommentRepository.save(comment);
        
        return new VideoCommentDto(
            savedComment.getId(),
            savedComment.getContent(),
            savedComment.getUser().getName(),
            savedComment.getCreatedAt().toString()
        );
    }

    // 비디오 삭제
    @Transactional
    public void deleteVideo(Long videoId, Long userId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("비디오를 찾을 수 없습니다."));
        
        // 권한 확인: 비디오 업로더만 삭제 가능
        if (!video.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("비디오를 삭제할 권한이 없습니다.");
        }
        
        // FK 제약 조건을 고려한 삭제 순서
        // 1. TimelineFeedback 삭제 (VideoTimeline을 참조)
        // 2. VideoComment 삭제 (Video를 참조)
        // 3. VideoLikes 삭제 (Video를 참조)
        // 4. VideoTimeline 삭제 (Video를 참조)
        // 5. Video 삭제
        
        // TimelineFeedback 삭제 (VideoTimeline의 ID를 사용)
        List<VideoTimeline> timelines = timelineRepository.findByVideoId(videoId);
        for (VideoTimeline timeline : timelines) {
            try {
                timelineFeedbackRepository.deleteByTimelineId(timeline.getId());
            } catch (Exception e) {
                System.err.println("TimelineFeedback 삭제 중 오류: " + e.getMessage());
            }
        }
        
        // VideoComment 삭제
        videoCommentRepository.deleteByVideoId(videoId);
        
        // VideoLikes 삭제
        likesRepository.deleteByVideo(video);
        
        // VideoTimeline 삭제
        timelineRepository.deleteByVideoId(videoId);
        
        // 마지막으로 Video 삭제
        videoRepository.delete(video);
    }
}
