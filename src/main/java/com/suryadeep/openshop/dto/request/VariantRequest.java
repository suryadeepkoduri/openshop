package com.suryadeep.openshop.dto.request;

import lombok.Data;

@Data
public class VariantRequest {
    private String name;
    private double price;
    private int stockQuantity;
}
