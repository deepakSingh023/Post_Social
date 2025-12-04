package com.example.social_post.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.account-id}")
    private String accountId;

    public String uploadBytes(byte[] bytes, String originalName, String contentType) {
        String key = UUID.randomUUID() + "-" + originalName;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(req, RequestBody.fromBytes(bytes));

        return "https://" + accountId + ".r2.cloudflarestorage.com/" + bucket + "/" + key;
    }

    public void deleteFile(String fileUrl) {
        try {
            // Extract key from url
            String key = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            DeleteObjectRequest req = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(req);

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete R2 file: " + e.getMessage());
        }
    }
}





