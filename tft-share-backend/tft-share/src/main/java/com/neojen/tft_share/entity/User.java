package com.neojen.tft_share.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.neojen.tft_share.enums.Tier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "profile_img", length = 512)
    private String profileImg;
    
    @Column(name = "google_access_token")
    private String googleAccessToken;

    @Column(name = "google_refresh_token")
    private String googleRefreshToken;

    @Column(nullable = false, length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'user'")
    private String role = "user";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tier tier = Tier.UNRANKED;
    
    @Column(name = "summoner_name", length = 100)
    private String summonerName;
    
    @Column(name = "summoner_verified", columnDefinition = "boolean default false")
    private Boolean summonerVerified = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
