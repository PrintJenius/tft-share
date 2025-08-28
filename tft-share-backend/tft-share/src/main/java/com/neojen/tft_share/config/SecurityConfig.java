package com.neojen.tft_share.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.neojen.tft_share.filter.JwtAuthenticationFilter;
import com.neojen.tft_share.security.JwtTokenProvider;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtTokenProvider);

        http
            .cors().and()  // CORS 활성화
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                // 헬스 체크 및 루트 경로 허용 (로드밸런서 헬스 체크용)
                .requestMatchers("/").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                .requestMatchers("/api/oauth2/**").permitAll()
                .requestMatchers("/api/video/list").permitAll()  // 동영상 목록은 인증 없이 접근 가능
                .requestMatchers("/api/video/list/paged").permitAll()  // 페이징된 동영상 목록은 인증 없이 접근 가능
                .requestMatchers("/api/video/*/stats").permitAll()  // 동영상 통계는 인증 없이 접근 가능
                .requestMatchers("/api/video/*/view").permitAll()   // 조회수 증가는 인증 없이 접근 가능
                .requestMatchers("/api/video/*/like").authenticated()  // 좋아요는 인증 필요
                .requestMatchers("/api/video/*/comment").authenticated()  // 댓글은 인증 필요
                .requestMatchers("/api/video/*").permitAll()     // 기타 동영상 정보는 인증 없이 접근 가능
                .requestMatchers("/api/timeline-feedback/**").permitAll()  // 타임라인 피드백은 인증 없이 접근 가능
                .requestMatchers("/api/users/me").authenticated()  // 현재 사용자 정보는 인증 필요
                .requestMatchers("/api/users/**").authenticated()  // 기타 사용자 API는 인증 필요
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // 개발환경 (주석처리)
        // configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        // 배포환경
        configuration.setAllowedOrigins(Arrays.asList(
            "http://tftshare.com", 
            "https://tftshare.com",
            "http://www.tftshare.com",
            "https://www.tftshare.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}