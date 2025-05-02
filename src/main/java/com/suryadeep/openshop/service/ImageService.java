package com.suryadeep.openshop.service;

import com.suryadeep.openshop.entity.Image;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Service
public interface ImageService {
    Image saveImage(MultipartFile file, String folder) throws IOException;
    void deleteImage(Long imageId);
    List<Image> saveImages(List<MultipartFile> files, String folder) throws IOException;
}