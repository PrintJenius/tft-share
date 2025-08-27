package com.neojen.tft_share.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.neojen.tft_share.entity.User;
import com.neojen.tft_share.entity.Video;
import com.neojen.tft_share.entity.VideoLikes;

public interface VideoLikesRepository extends JpaRepository<VideoLikes, Long> {
    
    Optional<VideoLikes> findByUserAndVideo(User user, Video video);
    
    boolean existsByUserAndVideo(User user, Video video);
    
    @Query("SELECT COUNT(vl) FROM VideoLikes vl WHERE vl.video = :video")
    long countByVideo(@Param("video") Video video);
    
    void deleteByUserAndVideo(User user, Video video);
    
    void deleteByVideo(Video video);
}
