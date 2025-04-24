package com.suryadeep.openshop.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Long id;
    private VariantResponse variant;
    private int quantity;
    private BigDecimal price;
}
