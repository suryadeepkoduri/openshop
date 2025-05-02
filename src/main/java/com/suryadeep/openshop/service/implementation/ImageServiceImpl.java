package com.suryadeep.openshop.service.implementation;


import com.suryadeep.openshop.entity.Image;
import com.suryadeep.openshop.repository.ImageRepository;
import com.suryadeep.openshop.service.ImageService;
import com.suryadeep.openshop.service.S3Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {
    
    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    @Override
    @Transactional
    public Image saveImage(MultipartFile file, String folder) throws IOException {
        String imageUrl = s3Service.uploadFile(file, folder);
        Image image = new Image();
        image.setImageKey(imageUrl);
        image.setName(file.getOriginalFilename());
        return imageRepository.save(image);
    }

    @Override
    @Transactional
    public List<Image> saveImages(List<MultipartFile> files, String folder) throws IOException {
        List<Image> images = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                images.add(saveImage(file, folder));
            }
        }
        return images;
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        imageRepository.findById(imageId).ifPresent(image -> {
            s3Service.deleteFile(image.getImageKey());
            imageRepository.delete(image);
        });
    }
}