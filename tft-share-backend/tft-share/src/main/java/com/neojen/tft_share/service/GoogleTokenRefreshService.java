package com.neojen.tft_share.service;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.neojen.tft_share.entity.User;
import com.neojen.tft_share.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleTokenRefreshService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    /**
     * Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.
     */
    public String refreshAccessToken(String refreshToken) throws Exception {
        URI tokenUri = URI.create("https://oauth2.googleapis.com/token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = UriComponentsBuilder.newInstance()
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("refresh_token", refreshToken)
                .queryParam("grant_type", "refresh_token")
                .build()
                .toUri()
                .getQuery();

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("토큰 갱신 실패: " + response.getStatusCode());
        }

        Map<String, Object> tokenMap = response.getBody();
        String newAccessToken = (String) tokenMap.get("access_token");
        
        // 새로운 refresh token이 있다면 업데이트
        String newRefreshToken = (String) tokenMap.get("refresh_token");
        if (newRefreshToken != null) {
            // DB에 새로운 refresh token 저장
            updateUserRefreshToken(refreshToken, newRefreshToken);
        }

        return newAccessToken;
    }

    /**
     * 사용자의 Access Token이 유효한지 확인하고, 만료되었다면 갱신합니다.
     */
    public String getValidAccessToken(User user) throws Exception {
        if (user.getGoogleAccessToken() == null) {
            throw new Exception("Google Access Token이 없습니다.");
        }

        if (user.getGoogleRefreshToken() == null) {
            throw new Exception("Google Refresh Token이 없습니다.");
        }

        try {
            // 현재 Access Token으로 간단한 API 호출 테스트
            testAccessToken(user.getGoogleAccessToken());
            return user.getGoogleAccessToken();
        } catch (Exception e) {
            // Access Token이 만료된 경우 Refresh Token으로 갱신
            String newAccessToken = refreshAccessToken(user.getGoogleRefreshToken());
            
            // DB에 새로운 Access Token 저장
            user.setGoogleAccessToken(newAccessToken);
            userRepository.save(user);
            
            return newAccessToken;
        }
    }

    /**
     * Access Token의 유효성을 테스트합니다.
     */
    private void testAccessToken(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + accessToken,
                HttpMethod.GET,
                request,
                Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Access Token이 유효하지 않습니다.");
        }
    }

    /**
     * 사용자의 Refresh Token을 업데이트합니다.
     */
    private void updateUserRefreshToken(String oldRefreshToken, String newRefreshToken) {
        userRepository.findByGoogleRefreshToken(oldRefreshToken)
                .ifPresent(user -> {
                    user.setGoogleRefreshToken(newRefreshToken);
                    userRepository.save(user);
                });
    }
}

