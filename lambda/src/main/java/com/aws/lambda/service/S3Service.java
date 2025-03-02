package com.aws.lambda.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    private AmazonS3 s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    // ✅ Use @PostConstruct to initialize S3Client after properties are loaded
    @PostConstruct
    public void initializeS3Client() {
        try {
            if (region == null || region.isEmpty()) {
                throw new IllegalStateException("AWS region is not set. Check application.properties.");
            }
            logger.info("Initializing Amazon S3 Client for region: {}", region);
            this.s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.fromName(region))
                    .withCredentials(new DefaultAWSCredentialsProviderChain()) // Uses AWS CLI credentials
                    .build();
            logger.info("Amazon S3 Client initialized successfully.");
        } catch (Exception e) {
            logger.error("Error initializing S3 client: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize S3 client", e);
        }
    }

    public String uploadFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        logger.info("Received file for upload: {}", fileName);

        File convertedFile = null;
        try {
            convertedFile = convertMultiPartToFile(file);
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, convertedFile));
            logger.info("File uploaded successfully: {}", fileName);
            return "File uploaded successfully: " + fileName;
        } catch (IOException e) {
            logger.error("Error converting file: {}", fileName, e);
            throw new RuntimeException("File conversion failed", e);
        } catch (Exception e) {
            logger.error("Error uploading file to S3: {}", fileName, e);
            throw new RuntimeException("S3 upload failed", e);
        } finally {
            if (convertedFile != null && convertedFile.exists()) {
                boolean deleted = convertedFile.delete();
                if (deleted) {
                    logger.info("Temporary file deleted: {}", convertedFile.getAbsolutePath());
                } else {
                    logger.warn("Failed to delete temporary file: {}", convertedFile.getAbsolutePath());
                }
            }
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        logger.info("Converted MultipartFile to File: {}", convFile.getAbsolutePath());
        return convFile;
    }
}
