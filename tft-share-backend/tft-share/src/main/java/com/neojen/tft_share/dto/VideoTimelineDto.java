package com.neojen.tft_share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoTimelineDto {
    private Long id;
    private Integer startTime;
    private Integer endTime;
    private String content;
}
