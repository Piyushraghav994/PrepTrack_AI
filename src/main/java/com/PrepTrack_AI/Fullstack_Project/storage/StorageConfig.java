package com.PrepTrack_AI.Fullstack_Project.storage;

import com.cloudinary.Cloudinary;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class that instantiates storage clients conditionally.
 */
@Configuration
@Slf4j
public class StorageConfig {

    // AWS S3 Properties
    @Value("${aws.accessKey:}")
    private String awsAccessKey;

    @Value("${aws.secretKey:}")
    private String awsSecretKey;

    @Value("${aws.s3.region:us-east-1}")
    private String awsRegion;

    // Cloudinary Properties
    @Value("${cloudinary.cloud-name:}")
    private String cloudinaryCloudName;

    @Value("${cloudinary.api-key:}")
    private String cloudinaryApiKey;

    @Value("${cloudinary.api-secret:}")
    private String cloudinaryApiSecret;

    // MinIO Properties
    @Value("${minio.url:}")
    private String minioUrl;

    @Value("${minio.access-key:}")
    private String minioAccessKey;

    @Value("${minio.secret-key:}")
    private String minioSecretKey;

    /**
     * Instantiates the S3Client bean.
     */
    @Bean
    public S3Client s3Client() {
        if (awsAccessKey == null || awsAccessKey.isBlank() || awsAccessKey.contains("your-aws")) {
            log.warn("AWS S3 access credentials not configured. S3StorageService will run in simulated mode.");
            return null;
        }
        try {
            return S3Client.builder()
                    .region(Region.of(awsRegion))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
                    ))
                    .build();
        } catch (Exception e) {
            log.error("Failed to build S3Client. S3 will run in simulated mode.", e);
            return null;
        }
    }

    /**
     * Instantiates the Cloudinary client bean.
     */
    @Bean
    public Cloudinary cloudinaryClient() {
        if (cloudinaryCloudName == null || cloudinaryCloudName.isBlank() || cloudinaryCloudName.contains("your-cloudinary")) {
            log.warn("Cloudinary credentials not configured. CloudinaryStorageService will run in simulated mode.");
            return null;
        }
        try {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", cloudinaryCloudName);
            config.put("api_key", cloudinaryApiKey);
            config.put("api_secret", cloudinaryApiSecret);
            return new Cloudinary(config);
        } catch (Exception e) {
            log.error("Failed to initialize Cloudinary client. Cloudinary will run in simulated mode.", e);
            return null;
        }
    }

    /**
     * Instantiates the MinIO client bean.
     */
    @Bean
    public MinioClient minioClient() {
        if (minioUrl == null || minioUrl.isBlank() || minioUrl.contains("your-minio") || minioUrl.contains("localhost")) {
            log.warn("MinIO client url is not configured or set to default local. MinioStorageService will check availability on startup.");
        }
        if (minioAccessKey == null || minioAccessKey.isBlank() || minioAccessKey.contains("your-minio")) {
            log.warn("MinIO credentials not configured. MinioStorageService will run in simulated mode.");
            return null;
        }
        try {
            return MinioClient.builder()
                    .endpoint(minioUrl)
                    .credentials(minioAccessKey, minioSecretKey)
                    .build();
        } catch (Exception e) {
            log.error("Failed to build MinioClient. MinIO will run in simulated mode.", e);
            return null;
        }
    }
}
