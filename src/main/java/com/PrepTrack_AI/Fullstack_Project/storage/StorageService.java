package com.PrepTrack_AI.Fullstack_Project.storage;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Interface defining storage operations for cloud storage providers.
 */
public interface StorageService {

    /**
     * Uploads a file to the active cloud storage provider.
     *
     * @param file The file to upload
     * @param folder The folder/directory path in the storage bucket
     * @return The public URL of the uploaded file
     * @throws IOException If upload fails due to network or format issues
     */
    String uploadFile(MultipartFile file, String folder) throws IOException;

    /**
     * Deletes a file from the active cloud storage provider.
     *
     * @param fileUrl The full public URL of the file to delete
     */
    void deleteFile(String fileUrl);
}
