package com.suryadeep.openshop.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VariantResponse {
    private Long id;
    private String name;
    private double price;
    private int stockQuantity;
}
