package com.suryadeep.openshop.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {
    /**
     * Upload a file to S3 and return the URL
     * 
     * @param file The file to upload
     * @param folderName The folder name in S3 bucket (e.g., "products", "categories")
     * @return The URL of the uploaded file
     * @throws IOException If there's an error reading the file
     */
    String uploadFile(MultipartFile file, String folderName) throws IOException;
    
    /**
     * Delete a file from S3
     * 
     * @param fileUrl The URL of the file to delete
     */
    void deleteFile(String fileUrl);
}