package com.neojen.tft_share.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.neojen.tft_share.entity.User;
import com.neojen.tft_share.security.JwtTokenProvider;
import com.neojen.tft_share.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/auth-url")
    public Map<String, String> getGoogleAuthUrl() {
        String authUrl = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("accounts.google.com")
                .path("/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile https://www.googleapis.com/auth/youtube.upload")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .build()
                .toUriString();
        
        System.out.println("Google Auth URL: " + authUrl);

        return Map.of("url", authUrl);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> oauth2Callback(@RequestParam String code) {

        // 1. 토큰 요청 설정
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

        // 2. 구글에서 토큰 요청
        ResponseEntity<Map> tokenResponse = restTemplate.exchange(tokenUri, HttpMethod.POST, request, Map.class);

        if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(tokenResponse.getStatusCode()).body("토큰 요청 실패");
        }

        Map<String, Object> tokenMap = tokenResponse.getBody();
        String accessToken = (String) tokenMap.get("access_token");
        String refreshToken = (String) tokenMap.get("refresh_token");

        // 3. 사용자 정보 요청
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);

        HttpEntity<?> userInfoRequest = new HttpEntity<>(userInfoHeaders);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://openidconnect.googleapis.com/v1/userinfo",
                HttpMethod.GET,
                userInfoRequest,
                Map.class);

        if (!userInfoResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(userInfoResponse.getStatusCode()).body("사용자 정보 조회 실패");
        }

        Map<String, Object> userInfo = userInfoResponse.getBody();

        String googleId = (String) userInfo.get("sub");  // 구글 고유 ID
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String profileImg = (String) userInfo.get("picture");

        // 4. DB 저장 또는 업데이트
        User user = userService.saveOrUpdateGoogleUser(googleId, email, name, profileImg, accessToken, refreshToken);

        // 5. JWT 토큰 생성 (여기서 user id를 payload로 넣음)
        String jwtToken = jwtTokenProvider.createToken(user.getId().toString());

        // 프론트엔드 URL로 리다이렉트 (토큰을 쿼리 파라미터로 전달)
        String frontendUrl = "http://localhost:5173/login-success?token=" + jwtToken;

        headers.setLocation(URI.create(frontendUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 Redirect
    }
}
