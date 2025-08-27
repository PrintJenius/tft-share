package com.neojen.tft_share.dto.riot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RiotSummonerDto {
    private String accountId;
    private int profileIconId;
    private long revisionDate;
    private String name;
    private String id;
    private String puuid;
    private long summonerLevel;
}

