package com.neojen.tft_share.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.neojen.tft_share.entity.Video;
import com.neojen.tft_share.service.VideoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoUploadController {

    private final VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("timeline") String timelineJson,
            @RequestParam("userId") Long userId
    ) {
        try {
            Video video = videoService.uploadVideo(file, title, description, timelineJson, userId);
            return ResponseEntity.ok(video);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("업로드 중 오류 발생: " + e.getMessage());
        }
    }
}
