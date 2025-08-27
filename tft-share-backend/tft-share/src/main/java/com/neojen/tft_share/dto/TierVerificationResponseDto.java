package com.neojen.tft_share.dto;

import com.neojen.tft_share.enums.Tier;
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
public class TierVerificationResponseDto {
    private boolean success;
    private String message;
    private Tier tier;
    private String division;  // I, II, III, IV
    private Integer lp;       // 리그 포인트
    private Integer wins;
    private Integer losses;
    private String summonerName;
}

