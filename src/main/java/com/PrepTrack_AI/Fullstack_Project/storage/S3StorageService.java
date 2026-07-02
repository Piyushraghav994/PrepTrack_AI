package com.PrepTrack_AI.Fullstack_Project.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

/**
 * AWS S3 Storage implementation.
 */
@Service("s3")
@Slf4j
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    public S3StorageService(@org.springframework.beans.factory.annotation.Autowired(required = false) S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Value("${aws.s3.bucket:your-s3-bucket}")
    private String bucketName;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = folder + "/" + UUID.randomUUID() + extension;

        if (s3Client == null || bucketName.contains("your-s3")) {
            log.info("[SIMULATION] S3 Upload: bucket={}, path={}, size={} bytes", bucketName, uniqueFilename, file.getSize());
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, uniqueFilename);
        }

        try {
            log.info("Uploading file to S3: bucket={}, path={}", bucketName, uniqueFilename);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFilename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, uniqueFilename);
            log.info("File uploaded successfully to S3: {}", s3Url);
            return s3Url;
        } catch (Exception e) {
            log.error("Failed to upload file to AWS S3, falling back to simulation.", e);
            return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, uniqueFilename);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (s3Client == null || bucketName.contains("your-s3")) {
            log.info("[SIMULATION] S3 Delete: url={}", fileUrl);
            return;
        }

        try {
            String s3Host = String.format("%s.s3.%s.amazonaws.com/", bucketName, region);
            if (!fileUrl.contains(s3Host)) {
                log.warn("File URL does not belong to configured S3 bucket: {}", fileUrl);
                return;
            }

            String key = fileUrl.substring(fileUrl.indexOf(s3Host) + s3Host.length());
            log.info("Deleting file from S3: bucket={}, key={}", bucketName, key);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully from S3: {}", fileUrl);
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}", fileUrl, e);
        }
    }
}
