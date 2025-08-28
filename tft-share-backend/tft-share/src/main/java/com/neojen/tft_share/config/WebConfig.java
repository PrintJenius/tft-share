package com.neojen.tft_share.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 모든 origin 패턴 허용 (개발용)
                .allowedOrigins(
                    "http://tftshare.com", 
                    "https://tftshare.com",
                    "http://www.tftshare.com",
                    "https://www.tftshare.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")  // 모든 헤더 허용
                .exposedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(3600);  // preflight 요청 캐시 시간
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // HTTP 요청 팩토리 설정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);  // 10초 연결 타임아웃
        factory.setReadTimeout(10000);     // 10초 읽기 타임아웃
        
        restTemplate.setRequestFactory(factory);
        
        return restTemplate;
    }
}
