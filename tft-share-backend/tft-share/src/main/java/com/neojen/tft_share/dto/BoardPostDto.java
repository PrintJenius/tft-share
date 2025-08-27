package com.neojen.tft_share.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostDto {

    private Long id; // 게시글 ID
    private String title; // 제목
    private String content; // 내용
    private String imageUrl;
    private Long categoryId; // 카테고리 ID
    private String categoryName; // 카테고리 이름
    private Long authorId; // 작성자 ID
    private String authorName; // 작성자 이름
    private LocalDateTime createdAt; // 작성일
    private LocalDateTime updatedAt; // 수정일
}
