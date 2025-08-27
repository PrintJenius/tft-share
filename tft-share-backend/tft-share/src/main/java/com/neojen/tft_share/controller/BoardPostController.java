package com.neojen.tft_share.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neojen.tft_share.dto.BoardPostDto;
import com.neojen.tft_share.service.BoardPostService;
import com.neojen.tft_share.service.S3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/board/posts")
@RequiredArgsConstructor
public class BoardPostController {

    private final BoardPostService boardPostService;
    private final S3Service s3Service;
    
    @Value("${aws.s3.bucket-name}")
    String bucketName;

    // 1. 게시글 목록 조회
    @GetMapping
    public ResponseEntity<Page<BoardPostDto>> getAllPosts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BoardPostDto> posts = boardPostService.getAllPosts(categoryId, keyword, pageable);
        return ResponseEntity.ok(posts);
    }

    // 2. 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<BoardPostDto> getPost(@PathVariable Long id) {
        BoardPostDto post = boardPostService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // 3. 게시글 작성
    @PostMapping
    public ResponseEntity<BoardPostDto> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
    	
        // S3에 업로드
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.uploadMultipartFile(bucketName, image);
            System.out.println(imageUrl);
        }

        // DTO 생성 후 서비스로 전달
        BoardPostDto postDto = new BoardPostDto();
        postDto.setTitle(title);
        postDto.setContent(content);
        postDto.setCategoryId(categoryId);
        postDto.setAuthorId(userId);
        postDto.setImageUrl(imageUrl);

        BoardPostDto createdPost = boardPostService.createPost(postDto);
        return ResponseEntity.ok(createdPost);
    }

    // 4. 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<BoardPostDto> updatePost(@PathVariable Long id, @RequestBody BoardPostDto postDto) {
        BoardPostDto updatedPost = boardPostService.updatePost(id, postDto);
        return ResponseEntity.ok(updatedPost);
    }

    // 5. 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        boardPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
