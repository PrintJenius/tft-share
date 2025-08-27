package com.neojen.tft_share.service;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GoogleOAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    // 1. code -> access token 요청
    public Map<String, Object> requestAccessToken(String code) {
        URI tokenUri = URI.create("https://oauth2.googleapis.com/token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = UriComponentsBuilder.newInstance()
                .queryParam("code", code)
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("grant_type", "authorization_code")
                .build()
                .toUri()
                .getQuery();

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return null;
        }
    }

    // 2. access token으로 사용자 정보 조회
    public Map<String, Object> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://openidconnect.googleapis.com/v1/userinfo",
                HttpMethod.GET,
                request,
                Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return null;
        }
    }
}
