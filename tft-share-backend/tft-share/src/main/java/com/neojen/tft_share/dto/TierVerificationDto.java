package com.neojen.tft_share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierVerificationDto {
    private String summonerName;  // 소환사 이름
    private String region;        // 리전 (KR, NA, EUW 등)
}

