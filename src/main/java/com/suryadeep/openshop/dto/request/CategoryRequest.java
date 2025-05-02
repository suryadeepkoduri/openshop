package com.suryadeep.openshop.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CategoryRequest {
    private String name;
    private String description;
    @JsonIgnore
    private List<MultipartFile> images;
}
