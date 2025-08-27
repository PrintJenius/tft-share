package com.neojen.tft_share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageResponseDto {
    private String profileImageUrl;
    private String message;
}
