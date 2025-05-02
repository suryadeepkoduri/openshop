package com.suryadeep.openshop.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private List<String> images;
}
