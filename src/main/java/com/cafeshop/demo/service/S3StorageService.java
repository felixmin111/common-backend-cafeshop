package com.cafeshop.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    private final AmazonS3 amazonS3;

    // Return BOTH key and url (so DB can store s3Key NOT NULL)
    public record UploadResult(String key, String url) {}

    public UploadResult uploadWithKey(MultipartFile file, Long menuItemId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (menuItemId == null || menuItemId <= 0) {
            throw new IllegalArgumentException("menuItemId is required");
        }

        String safeName = sanitizeFilename(file.getOriginalFilename());
        String key = "menuItems/" + menuItemId + "/" + UUID.randomUUID() + "-" + safeName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(bucket, key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file stream: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to S3: " + e.getMessage(), e);
        }

        // Build URL (works if bucket/object is publicly readable)
        String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
        return new UploadResult(key, url);
    }

    // If you still want a simple method, require menuItemId
    public String upload(MultipartFile file, Long menuItemId) {
        return uploadWithKey(file, menuItemId).url();
    }

    public void delete(String key) {
        if (key == null || key.isBlank()) return;
        amazonS3.deleteObject(bucket, key);
    }

    private static String sanitizeFilename(String name) {
        if (name == null || name.isBlank()) return "file";
        // remove path separators, keep simple safe chars
        String cleaned = name.replace("\\", "_").replace("/", "_").trim();
        // optional: lower-case
        return cleaned.toLowerCase(Locale.ROOT);
    }
}