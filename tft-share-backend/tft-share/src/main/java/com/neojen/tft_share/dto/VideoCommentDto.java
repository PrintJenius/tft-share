package com.neojen.tft_share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoCommentDto {
    private Long id;
    private String content;
    private String userName;
    private String createdAt;
}
