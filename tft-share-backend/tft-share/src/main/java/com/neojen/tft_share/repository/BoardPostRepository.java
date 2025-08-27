package com.neojen.tft_share.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.neojen.tft_share.entity.BoardPost;

@Repository
public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

	Page<BoardPost> findByCategory_Id(Long categoryId, Pageable pageable);
    Page<BoardPost> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<BoardPost> findByCategory_IdAndTitleContainingIgnoreCase(Long categoryId, String keyword, Pageable pageable);
}
