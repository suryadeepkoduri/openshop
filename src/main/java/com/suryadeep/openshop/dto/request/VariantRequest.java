package com.suryadeep.openshop.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VariantRequest {
    private String name;
    private BigDecimal price;
    private int stockQuantity;
}
