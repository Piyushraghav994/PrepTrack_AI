package com.PrepTrack_AI.Fullstack_Project.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * MinIO Storage implementation.
 */
@Service("minio")
@Slf4j
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;

    public MinioStorageService(@org.springframework.beans.factory.annotation.Autowired(required = false) MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Value("${minio.bucket:your-minio-bucket}")
    private String bucketName;

    @Value("${minio.url:http://localhost:9000}")
    private String minioUrl;

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = folder + "/" + UUID.randomUUID() + extension;

        if (minioClient == null || bucketName.contains("your-minio")) {
            log.info("[SIMULATION] MinIO Upload: bucket={}, path={}, size={} bytes", bucketName, uniqueFilename, file.getSize());
            return String.format("%s/%s/%s", minioUrl, bucketName, uniqueFilename);
        }

        try {
            log.info("Uploading file to MinIO: bucket={}, path={}", bucketName, uniqueFilename);
            
            // Check if bucket exists, create if not
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFilename)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String fileUrl = String.format("%s/%s/%s", minioUrl, bucketName, uniqueFilename);
            log.info("File uploaded successfully to MinIO: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO, falling back to simulation.", e);
            return String.format("%s/%s/%s", minioUrl, bucketName, uniqueFilename);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (minioClient == null || bucketName.contains("your-minio")) {
            log.info("[SIMULATION] MinIO Delete: url={}", fileUrl);
            return;
        }

        try {
            String prefix = bucketName + "/";
            if (!fileUrl.contains(prefix)) {
                log.warn("File URL does not contain bucket prefix: {}", fileUrl);
                return;
            }

            String objectName = fileUrl.substring(fileUrl.indexOf(prefix) + prefix.length());
            log.info("Deleting file from MinIO: bucket={}, object={}", bucketName, objectName);
            
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("File deleted successfully from MinIO: {}", fileUrl);
        } catch (Exception e) {
            log.error("Failed to delete file from MinIO: {}", fileUrl, e);
        }
    }
}
