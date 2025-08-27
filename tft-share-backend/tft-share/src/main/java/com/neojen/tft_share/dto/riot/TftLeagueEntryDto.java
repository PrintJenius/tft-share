package com.neojen.tft_share.dto.riot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TftLeagueEntryDto {
    private String puuid;  // Player Universal Unique Identifier
    private String leagueId;
    private String summonerId;
    private String summonerName;
    private String queueType;
    private String tier;
    private String rank;
    private int leaguePoints;
    private int wins;
    private int losses;
    private boolean hotStreak;
    private boolean veteran;
    private boolean freshBlood;
    private boolean inactive;
    private MiniSeriesDto miniSeries;
    
    @Data
    public static class MiniSeriesDto {
        private int losses;
        private String progress;
        private int target;
        private int wins;
    }
}

