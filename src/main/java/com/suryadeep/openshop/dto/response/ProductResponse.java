package com.suryadeep.openshop.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private List<String> imageUrls;     // Simplified response with URLs or names
    private boolean isEnabled;
    private String categoryName;
    private List<VariantResponse> variants;
}
