package com.neojen.tft_share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VideoStatsDto {
    private Long videoId;
    private Integer views;
    private Integer likes;
    private Boolean isLikedByUser;
}
