package com.neojen.tft_share.dto.riot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RiotAccountDto {
    private String puuid;
    private String gameName;
    private String tagLine;
}

