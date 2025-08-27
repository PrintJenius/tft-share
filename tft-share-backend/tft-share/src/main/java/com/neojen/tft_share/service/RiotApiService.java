package com.neojen.tft_share.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.neojen.tft_share.dto.riot.RiotAccountDto;
import com.neojen.tft_share.dto.riot.TftLeagueEntryDto;
import com.neojen.tft_share.enums.Tier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiotApiService {

    private final RestTemplate restTemplate;

    @Value("${riot.api.key}")
    private String riotApiKey;

    @Value("${riot.api.asia-url}")
    private String asiaUrl;

    @Value("${riot.api.kr-url}")
    private String krUrl;

    @PostConstruct
    public void init() {
        log.info("RiotApiService 초기화 - Asia URL: {}, KR URL: {}", asiaUrl, krUrl);
        log.info("API Key (앞 10자리): {}", riotApiKey != null ? riotApiKey.substring(0, Math.min(10, riotApiKey.length())) + "..." : "NULL");
    }

    /**
     * Riot ID로 계정 정보 조회 (1단계)
     */
    public Optional<RiotAccountDto> getPuuidByRiotId(String summonerName) {
        try {
            // 1. Riot ID 파싱 (게임명#태그 형식)
            String[] parts = summonerName.split("#");
            if (parts.length != 2) {
                log.error("잘못된 Riot ID 형식: {} (예: 게임명#태그)", summonerName);
                return Optional.empty();
            }

            String gameName = parts[0].trim();
            String tagLine = parts[1].trim();

            if (gameName.isEmpty() || tagLine.isEmpty()) {
                log.error("게임명 또는 태그가 비어있음: 게임명='{}', 태그='{}'", gameName, tagLine);
                return Optional.empty();
            }
            
            log.info("=== Riot ID API 호출 시작 ===");
            log.info("원본 Riot ID: '{}'", summonerName);
            log.info("게임명: '{}', 태그: '{}'", gameName, tagLine);
            log.info("인코딩된 게임명: '{}'", gameName);
            
            // 3. Riot ID API 호출 (글로벌 서버 사용) - 헤더로 API 키 전송
            String accountUrl = String.format("%s/riot/account/v1/accounts/by-riot-id/%s/%s", 
                                            asiaUrl, gameName, tagLine);
            
            log.info("Riot ID API 호출 URL: {}", accountUrl);
            
            // Riot Account API는 헤더로 API 키 전송
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Riot-Token", riotApiKey);
            headers.set("User-Agent", "Mozilla/5.0");
            headers.set("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            headers.set("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8");
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<RiotAccountDto> accountResponse = restTemplate.exchange(
                accountUrl, HttpMethod.GET, entity, RiotAccountDto.class);
            
            log.info("=== API 응답 수신 ===");
            log.info("응답 상태: {}", accountResponse.getStatusCode());
            log.info("응답 본문: {}", accountResponse.getBody());
            
            if (accountResponse.getBody() == null) {
                log.error("계정 정보 조회 실패 - 응답 본문이 null");
                return Optional.empty();
            }

            RiotAccountDto accountDto = accountResponse.getBody();
            if (accountDto.getPuuid() == null || accountDto.getPuuid().isEmpty()) {
                log.error("PUUID가 null이거나 비어있음: {}", accountDto.getPuuid());
                return Optional.empty();
            }
            
            log.info("✅ 계정 정보 획득 성공: {}", accountDto);
            return Optional.of(accountDto);
            
        } catch (HttpClientErrorException e) {
            try {
                String responseBody = e.getResponseBodyAsString();
                log.error("계정 정보 조회 실패 - HTTP 상태: {}, 응답: {}", e.getStatusCode(), responseBody);
            } catch (Exception ex) {
                log.error("계정 정보 조회 실패 - HTTP 상태: {}, 응답 읽기 실패: {}", e.getStatusCode(), ex.getMessage());
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("계정 정보 조회 중 예상치 못한 오류 발생", e);
            return Optional.empty();
        }
    }

    /**
     * PUUID로 TFT 리그 정보 조회 (2단계)
     */
    public Optional<TftLeagueEntryDto> getTftLeagueByPuuid(String puuid) {
        try {
            // TFT 리그 정보 조회 - 올바른 엔드포인트
            String tftUrl = String.format("%s/tft/league/v1/by-puuid/%s", krUrl, puuid);
            
            log.info("TFT 리그 API 호출: {}", tftUrl);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0");
            headers.set("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
            headers.set("Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8");
            headers.set("X-Riot-Token", riotApiKey);  // TFT API는 헤더로 API 키 전송
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Set<TftLeagueEntryDto>> tftResponse = restTemplate.exchange(
                tftUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<Set<TftLeagueEntryDto>>() {});
            
            if (tftResponse.getBody() != null && !tftResponse.getBody().isEmpty()) {
                // RANKED_TFT (일반 랭크) 우선 선택, 더블업 무시
                TftLeagueEntryDto rankedTftEntry = tftResponse.getBody().stream()
                    .filter(entry -> "RANKED_TFT".equals(entry.getQueueType()))
                    .findFirst()
                    .orElse(null);
                
                if (rankedTftEntry != null) {
                    log.info("✅ TFT 일반 랭크 정보 조회 성공: {}", rankedTftEntry);
                    return Optional.of(rankedTftEntry);
                } else {
                    // RANKED_TFT가 없으면 (더블업만 있으면) 언랭크로 처리
                    log.info("ℹ️ TFT 일반 랭크 없음, 더블업만 존재 - 언랭크로 처리");
                    return Optional.empty();
                }
            }
            
            log.info("ℹ️ TFT 리그 정보 없음 (언랭크)");
            return Optional.empty();
            
        } catch (HttpClientErrorException e) {
            try {
                String responseBody = e.getResponseBodyAsString();
                log.error("TFT 리그 조회 실패 - HTTP 상태: {}, 응답: {}", e.getStatusCode(), responseBody);
            } catch (Exception ex) {
                log.error("TFT 리그 조회 실패 - HTTP 상태: {}, 응답 읽기 실패: {}", e.getStatusCode(), ex.getMessage());
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("TFT 리그 조회 중 예상치 못한 오류 발생", e);
            return Optional.empty();
        }
    }

    /**
     * Riot ID로 TFT 티어 정보 조회 (메인 메서드 - 1단계 + 2단계 조합)
     */
    public Optional<TftLeagueEntryDto> getTftTierByRiotId(String summonerName) {
        log.info("=== TFT 티어 조회 시작: {} ===", summonerName);
        
        // 1단계: Riot ID → 계정 정보
        Optional<RiotAccountDto> accountOpt = getPuuidByRiotId(summonerName);
        if (accountOpt.isEmpty()) {
            log.warn("1단계 실패: 계정 정보 조회 실패");
            return Optional.empty();
        }
        
        // 2단계: PUUID → TFT 리그 정보
        Optional<TftLeagueEntryDto> tftEntryOpt = getTftLeagueByPuuid(accountOpt.get().getPuuid());
        if (tftEntryOpt.isPresent()) {
            log.info("✅ TFT 티어 조회 완료: {}", tftEntryOpt.get());
            return tftEntryOpt;
        } else {
            // TFT 리그 정보가 없으면 언랭크로 처리
            log.info("ℹ️ TFT 리그 정보 없음 - 언랭크로 처리");
            TftLeagueEntryDto unrankedEntry = new TftLeagueEntryDto();
            unrankedEntry.setPuuid(accountOpt.get().getPuuid());
            unrankedEntry.setTier("UNRANKED");
            unrankedEntry.setRank("");
            unrankedEntry.setLeaguePoints(0);
            unrankedEntry.setWins(0);
            unrankedEntry.setLosses(0);
            return Optional.of(unrankedEntry);
        }
    }

    /**
     * Riot API 티어를 내부 Tier enum으로 변환
     */
    public Tier convertRiotTierToEnum(String riotTier) {
        if (riotTier == null) {
            return Tier.UNRANKED;
        }

        return switch (riotTier.toUpperCase()) {
            case "IRON" -> Tier.IRON;
            case "BRONZE" -> Tier.BRONZE;
            case "SILVER" -> Tier.SILVER;
            case "GOLD" -> Tier.GOLD;
            case "PLATINUM" -> Tier.PLATINUM;
            case "EMERALD" -> Tier.EMERALD;
            case "DIAMOND" -> Tier.DIAMOND;
            case "MASTER" -> Tier.MASTER;
            case "GRANDMASTER" -> Tier.GRANDMASTER;
            case "CHALLENGER" -> Tier.CHALLENGER;
            default -> Tier.UNRANKED;
        };
    }
}
