package com.suryadeep.openshop.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductRequest {
    @NotBlank
    private String name;
    private String description;
    private Long categoryId;
    private List<MultipartFile> images;
    private List<VariantRequest> variants;

}

// name, description,category,variants