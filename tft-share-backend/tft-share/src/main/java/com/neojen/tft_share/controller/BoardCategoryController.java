package com.neojen.tft_share.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neojen.tft_share.dto.BoardCategoryDto;
import com.neojen.tft_share.service.BoardCategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/board/categories")
@RequiredArgsConstructor
public class BoardCategoryController {

    private final BoardCategoryService categoryService;

    // 전체 카테고리 조회
    @GetMapping
    public ResponseEntity<List<BoardCategoryDto>> getAllCategories() {
        List<BoardCategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}
