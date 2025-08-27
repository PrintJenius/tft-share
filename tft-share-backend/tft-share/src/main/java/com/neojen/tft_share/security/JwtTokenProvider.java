package com.neojen.tft_share.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    public String createToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))  // secretKey는 byte[] 또는 Key 타입이어야 함
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 유효성 검사
	public boolean validateToken(String token) {
	    try {
	        Jwts.parserBuilder()
	            .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
	            .build()
	            .parseClaimsJws(token);
	        return true;
	    } catch (JwtException | IllegalArgumentException e) {
	        // 토큰이 잘못됐거나 만료됨
	        return false;
	    }
	}
}
