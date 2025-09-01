package com.neojen.tft_share.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.neojen.tft_share.dto.ProfileImageResponseDto;
import com.neojen.tft_share.dto.ProfileUpdateDto;
import com.neojen.tft_share.dto.UserDto;
import com.neojen.tft_share.dto.TierVerificationDto;
import com.neojen.tft_share.dto.TierVerificationResponseDto;
import com.neojen.tft_share.service.UserService;
import com.neojen.tft_share.service.S3Service;
import com.neojen.tft_share.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;
    private final JwtTokenProvider jwtTokenProvider;

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(@RequestBody ProfileUpdateDto profileUpdateDto) {
        Long userId = getCurrentUserId();
        UserDto updatedUser = userService.updateProfile(userId, profileUpdateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        Long userId = getCurrentUserId();
        UserDto userProfile = userService.findById(userId);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile() {
        Long userId = getCurrentUserId();
        UserDto userProfile = userService.findById(userId);
        return ResponseEntity.ok(userProfile);
    }

    @PostMapping("/verify-tier")
    public ResponseEntity<TierVerificationResponseDto> verifyTftTier(@RequestBody TierVerificationDto verificationDto) {
        Long userId = getCurrentUserId();
        TierVerificationResponseDto response = userService.verifyTftTier(userId, verificationDto);
        return ResponseEntity.ok(response);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        
        // JWT 토큰에서 사용자 ID를 추출
        String userId = authentication.getName();
        try {
            return Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("유효하지 않은 사용자 ID입니다: " + userId);
        }
    }
}
