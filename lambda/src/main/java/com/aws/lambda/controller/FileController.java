package com.aws.lambda.controller;

import com.aws.lambda.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("Received upload request for file: {}", file.getOriginalFilename());
            String message = s3Service.uploadFile(file);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            logger.error("Upload failed: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
}
