package com.neojen.tft_share.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 게시판 카테고리 DTO
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategoryDto {
    private Long id;
    private String name;
}
