package com.neojen.tft_share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.neojen.tft_share.entity.Video;

public interface VideoRepository extends JpaRepository<Video, Long>{
    
    // 최신순으로 정렬된 비디오 목록 조회
    @Query("SELECT v FROM Video v ORDER BY v.createdAt DESC")
    List<Video> findAllByOrderByCreatedAtDesc();
}
