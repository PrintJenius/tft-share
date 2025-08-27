package com.neojen.tft_share.dto;

import java.util.List;

import com.neojen.tft_share.dto.VideoTimelineDto;
import com.neojen.tft_share.dto.VideoCommentDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoDetailDto {
    private Long id;
    private Long userId;  // 비디오 업로더의 사용자 ID 추가
    private String title;
    private String description;
    private String youtubeVideoId;
    private String thumbnailUrl;
    private String summonerName;
    private String tier;
    private List<VideoTimelineDto> timeline;
    private List<VideoCommentDto> comments;
}
