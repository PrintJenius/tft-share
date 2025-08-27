package com.neojen.tft_share.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VideoListDto {
    private Long id;
    private String title;
    private String youtubeVideoId;
    private String thumbnailUrl;
    private Integer views;
    private Integer likes;
    private Integer comments;
    private LocalDateTime createdAt;
    private String summonerName;
    private String tier;
}