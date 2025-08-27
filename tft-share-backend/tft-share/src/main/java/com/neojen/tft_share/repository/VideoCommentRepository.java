package com.neojen.tft_share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neojen.tft_share.entity.VideoComment;

public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {

	List<VideoComment> findByVideoId(Long videoId);
	
	long countByVideoId(Long videoId);
	
	void deleteByVideoId(Long videoId);
}
