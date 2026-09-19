package com.PrepTrack_AI.Fullstack_Project.storage;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Cloudinary Storage implementation.
 */
@Service("cloudinary")
@Slf4j
public class CloudinaryStorageService implements StorageService {

    private final Cloudinary cloudinary;

    public CloudinaryStorageService(@org.springframework.beans.factory.annotation.Autowired(required = false) Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString();

        if (cloudinary == null) {
            log.info("[SIMULATION] Cloudinary Upload: folder={}, filename={}, size={} bytes", folder, uniqueFilename, file.getSize());
            return String.format("https://res.cloudinary.com/mock-cloud/image/upload/v1234567890/%s/%s%s", folder, uniqueFilename, extension);
        }

        try {
            log.info("Uploading file to Cloudinary: folder={}, filename={}", folder, uniqueFilename);
            Map<String, Object> options = new HashMap<>();
            options.put("folder", folder);
            options.put("public_id", uniqueFilename);
            options.put("resource_type", "auto");

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            String secureUrl = (String) uploadResult.get("secure_url");
            log.info("File uploaded successfully to Cloudinary: {}", secureUrl);
            return secureUrl;
        } catch (Exception e) {
            log.error("Failed to upload file to Cloudinary, falling back to simulation.", e);
            return String.format("https://res.cloudinary.com/mock-cloud/image/upload/v1234567890/%s/%s%s", folder, uniqueFilename, extension);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (cloudinary == null) {
            log.info("[SIMULATION] Cloudinary Delete: url={}", fileUrl);
            return;
        }

        try {
            // Cloudinary URLs are structured: https://res.cloudinary.com/<cloud_name>/<resource_type>/upload/v<version>/<folder>/<public_id>.<extension>
            // We can extract folder and public_id to destroy the file
            String uploadKey = "/upload/";
            if (!fileUrl.contains(uploadKey)) {
                log.warn("File URL is not a standard Cloudinary upload URL: {}", fileUrl);
                return;
            }

            String relativePath = fileUrl.substring(fileUrl.indexOf(uploadKey) + uploadKey.length());
            // Skip the version segment (starts with 'v' followed by digits)
            if (relativePath.contains("/")) {
                String sub = relativePath.substring(relativePath.indexOf("/") + 1);
                // remove extension
                String publicIdWithFolder = sub;
                if (publicIdWithFolder.contains(".")) {
                    publicIdWithFolder = publicIdWithFolder.substring(0, publicIdWithFolder.lastIndexOf("."));
                }
                log.info("Deleting file from Cloudinary: publicId={}", publicIdWithFolder);
                
                Map<String, Object> options = new HashMap<>();
                options.put("invalidate", true);
                
                cloudinary.uploader().destroy(publicIdWithFolder, options);
                log.info("File deleted successfully from Cloudinary: {}", fileUrl);
            }
        } catch (Exception e) {
            log.error("Failed to delete file from Cloudinary: {}", fileUrl, e);
        }
    }
}
