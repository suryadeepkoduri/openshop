package com.suryadeep.openshop.service.implementation;

import com.suryadeep.openshop.service.S3Service;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // Generate a unique file name
        String fileName = folderName + "/" + UUID.randomUUID() + extension;
        
        // Upload file to S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
        
        // Generate and return the URL
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
        logger.info("File uploaded successfully: {}", fileUrl);
        
        return fileUrl;
    }


    @Override
    public void deleteFile(String fileUrl) {
        try {
            // Extract the key from the URL
            String key = fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);
            
            // Delete the file from S3
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            logger.info("File deleted successfully: {}", fileUrl);
        } catch (Exception e) {
            logger.error("Error deleting file from S3: {}", fileUrl, e);
            throw e;
        }
    }
}