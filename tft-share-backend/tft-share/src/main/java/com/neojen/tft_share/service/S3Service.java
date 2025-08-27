package com.neojen.tft_share.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.core.sync.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(String bucketName, String keyName, String filePath) {
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .serverSideEncryption(ServerSideEncryption.AES256)
                .build();

        s3Client.putObject(putRequest, java.nio.file.Paths.get(filePath));
    }

    public String uploadMultipartFile(String bucketName, MultipartFile file) throws IOException {
        String keyName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // 고유 이름

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .serverSideEncryption(ServerSideEncryption.AES256)
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // 업로드 후 URL 반환
        return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/" + keyName;
    }

    public String getBucketName() {
        return bucketName;
    }
}