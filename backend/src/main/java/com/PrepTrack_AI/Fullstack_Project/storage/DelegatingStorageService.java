package com.PrepTrack_AI.Fullstack_Project.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Primary storage service that delegates storage operations dynamically
 * to the configured provider (s3, cloudinary, or minio).
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class DelegatingStorageService implements StorageService {

    private final ApplicationContext applicationContext;

    @Value("${app.storage.provider:s3}")
    private String provider;

    private StorageService getActiveStorageService() {
        String serviceBeanName = provider.toLowerCase().trim();
        if (!applicationContext.containsBean(serviceBeanName)) {
            log.error("Storage provider bean '{}' not found. Defaulting to S3 simulation.", serviceBeanName);
            return applicationContext.getBean("s3", StorageService.class);
        }
        return applicationContext.getBean(serviceBeanName, StorageService.class);
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        return getActiveStorageService().uploadFile(file, folder);
    }

    @Override
    public void deleteFile(String fileUrl) {
        getActiveStorageService().deleteFile(fileUrl);
    }
}
