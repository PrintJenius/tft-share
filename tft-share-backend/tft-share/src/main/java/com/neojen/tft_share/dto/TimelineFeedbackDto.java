package com.neojen.tft_share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineFeedbackDto {
    private Long id;
    private String comment;
    private LocalDateTime createdAt;
    private Long timelineId;
    private Long userId;
    private String userName;
    private String userProfileImage;
    private String summonerName;  // 소환사명 추가
    private String tier;          // 티어 추가
}
