package com.neojen.tft_share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neojen.tft_share.entity.VideoTimeline;

public interface VideoTimelineRepository extends JpaRepository<VideoTimeline, Long>{

	List<VideoTimeline> findByVideoId(Long videoId);
	
	void deleteByVideoId(Long videoId);
}
