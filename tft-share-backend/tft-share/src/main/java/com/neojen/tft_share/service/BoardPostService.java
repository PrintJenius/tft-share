package com.neojen.tft_share.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neojen.tft_share.dto.BoardPostDto;
import com.neojen.tft_share.entity.BoardCategory;
import com.neojen.tft_share.entity.BoardPost;
import com.neojen.tft_share.entity.User;
import com.neojen.tft_share.repository.BoardCategoryRepository;
import com.neojen.tft_share.repository.BoardPostRepository;
import com.neojen.tft_share.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardPostService {

    private final BoardPostRepository boardPostRepository;
    private final BoardCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 전체 게시글 조회 (카테고리 필터 + 키워드 검색)
    public Page<BoardPostDto> getAllPosts(Long categoryId, String keyword, Pageable pageable) {
        Page<BoardPost> posts;

        if (categoryId != null && keyword != null) {
            posts = boardPostRepository.findByCategory_IdAndTitleContainingIgnoreCase(categoryId, keyword, pageable);
        } else if (categoryId != null) {
            posts = boardPostRepository.findByCategory_Id(categoryId, pageable);
        } else if (keyword != null) {
            posts = boardPostRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else {
            posts = boardPostRepository.findAll(pageable);
        }

        return posts.map(this::convertToDto);
    }

    // 단일 게시글 조회
    public BoardPostDto getPostById(Long id) {
        BoardPost post = boardPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        return convertToDto(post);
    }

    // 게시글 생성
    public BoardPostDto createPost(BoardPostDto postDto) {
        BoardCategory category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("유효한 카테고리가 아닙니다."));
        User author = userRepository.findById(postDto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("유효한 작성자가 아닙니다."));

        BoardPost post = BoardPost.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .imageUrl(postDto.getImageUrl())
                .category(category)
                .user(author)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        boardPostRepository.save(post);
        return convertToDto(post);
    }

    // 게시글 수정
    public BoardPostDto updatePost(Long id, BoardPostDto postDto) {
        BoardPost post = boardPostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (postDto.getTitle() != null) post.setTitle(postDto.getTitle());
        if (postDto.getContent() != null) post.setContent(postDto.getContent());
        if (postDto.getCategoryId() != null) {
            BoardCategory category = categoryRepository.findById(postDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("유효한 카테고리가 아닙니다."));
            post.setCategory(category);
        }

        post.setUpdatedAt(LocalDateTime.now());
        boardPostRepository.save(post);

        return convertToDto(post);
    }

    // 게시글 삭제
    public void deletePost(Long id) {
        if (!boardPostRepository.existsById(id)) {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
        boardPostRepository.deleteById(id);
    }

    // BoardPost -> BoardPostDto 변환
    private BoardPostDto convertToDto(BoardPost post) {
        return BoardPostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .categoryId(post.getCategory().getId())
                .categoryName(post.getCategory().getName())
                .authorId(post.getUser().getId())
                .authorName(post.getUser().getName())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
