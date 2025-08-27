package com.neojen.tft_share.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.neojen.tft_share.dto.BoardCategoryDto;
import com.neojen.tft_share.entity.BoardCategory;
import com.neojen.tft_share.repository.BoardCategoryRepository;

@Service
@RequiredArgsConstructor
public class BoardCategoryService {

    private final BoardCategoryRepository categoryRepository;

    public List<BoardCategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> new BoardCategoryDto(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }
}
