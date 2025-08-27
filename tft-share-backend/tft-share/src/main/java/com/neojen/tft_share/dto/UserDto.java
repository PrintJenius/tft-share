package com.neojen.tft_share.dto;

import java.time.LocalDateTime;

import com.neojen.tft_share.enums.Tier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String googleId;
    private String email;
    private String name;
    private String profileImg;
    private Tier tier;
    private String summonerName;
    private Boolean summonerVerified;
    private LocalDateTime createdAt;
}
