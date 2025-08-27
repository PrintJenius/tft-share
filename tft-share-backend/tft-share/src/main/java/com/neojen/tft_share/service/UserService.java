package com.neojen.tft_share.service;

import org.springframework.stereotype.Service;

import com.neojen.tft_share.dto.UserDto;
import com.neojen.tft_share.dto.ProfileUpdateDto;
import com.neojen.tft_share.dto.TierUpdateDto;
import com.neojen.tft_share.dto.ProfileImageResponseDto;
import com.neojen.tft_share.dto.TierVerificationDto;
import com.neojen.tft_share.dto.TierVerificationResponseDto;
import com.neojen.tft_share.entity.User;
import com.neojen.tft_share.repository.UserRepository;
import com.neojen.tft_share.service.RiotApiService;
import com.neojen.tft_share.enums.Tier;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RiotApiService riotApiService;

    public User saveOrUpdateGoogleUser(String googleId, String email, String name, String profileImg,
    								   String accessToken, String refreshToken) {
        return userRepository.findByGoogleId(googleId).map(user -> {
            // 업데이트가 필요하면 여기서 업데이트
            user.setEmail(email);
            user.setName(name);
            user.setProfileImg(profileImg);
            user.setGoogleAccessToken(accessToken);
            user.setGoogleRefreshToken(refreshToken);
            return userRepository.save(user);
        }).orElseGet(() -> {
            // 신규 등록
            User newUser = new User();
            newUser.setGoogleId(googleId);
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setProfileImg(profileImg);
            newUser.setGoogleAccessToken(accessToken);
            newUser.setGoogleRefreshToken(refreshToken);
            return userRepository.save(newUser);
        });
    }

    public UserDto findById(long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .googleId(user.getGoogleId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImg(user.getProfileImg())
                .tier(user.getTier())
                .summonerName(user.getSummonerName())
                .summonerVerified(user.getSummonerVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User findEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 유저가 없습니다. ID: " + userId));
    }



    public UserDto updateProfile(Long userId, ProfileUpdateDto profileUpdateDto) {
        User user = findEntityById(userId);
        user.setName(profileUpdateDto.getName());
        user.setEmail(profileUpdateDto.getEmail());
        
        User updatedUser = userRepository.save(user);
        return toDto(updatedUser);
    }

    public ProfileImageResponseDto updateProfileImage(Long userId, String imageUrl) {
        User user = findEntityById(userId);
        user.setProfileImg(imageUrl);
        
        User updatedUser = userRepository.save(user);
        return ProfileImageResponseDto.builder()
                .profileImageUrl(updatedUser.getProfileImg())
                .message("프로필 이미지가 성공적으로 업데이트되었습니다.")
                .build();
    }

    public UserDto findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
                .map(this::toDto)
                .orElse(null);
    }

    /**
     * TFT 티어 인증
     */
    public TierVerificationResponseDto verifyTftTier(Long userId, TierVerificationDto verificationDto) {
        User user = findEntityById(userId);
        if (user == null) {
            return TierVerificationResponseDto.builder()
                    .success(false)
                    .message("사용자를 찾을 수 없습니다.")
                    .build();
        }

        // 소환사명에 태그가 포함되어 있는지 확인
        if (verificationDto.getSummonerName() == null || !verificationDto.getSummonerName().contains("#")) {
            return TierVerificationResponseDto.builder()
                    .success(false)
                    .message("소환사명에 태그를 포함해주세요. (예: Jaebeob TFT#1111)")
                    .build();
        }

        try {
            // Riot API로 TFT 티어 정보 조회
            var tftEntryOpt = riotApiService.getTftTierByRiotId(verificationDto.getSummonerName());
            
            if (tftEntryOpt.isEmpty()) {
                return TierVerificationResponseDto.builder()
                        .success(false)
                        .message("소환사를 찾을 수 없거나 TFT 티어 정보가 없습니다.")
                        .build();
            }

            var tftEntry = tftEntryOpt.get();
            
            // 티어 정보 업데이트
            Tier tier = riotApiService.convertRiotTierToEnum(tftEntry.getTier());
            user.setTier(tier);
            user.setSummonerName(verificationDto.getSummonerName());
            user.setSummonerVerified(true);
            
            userRepository.save(user);

            return TierVerificationResponseDto.builder()
                    .success(true)
                    .message("티어 인증이 완료되었습니다.")
                    .tier(tier)
                    .division(tftEntry.getRank())
                    .lp(tftEntry.getLeaguePoints())
                    .wins(tftEntry.getWins())
                    .losses(tftEntry.getLosses())
                    .summonerName(verificationDto.getSummonerName())
                    .build();

        } catch (Exception e) {
            return TierVerificationResponseDto.builder()
                    .success(false)
                    .message("티어 인증 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }
}
