package com.suryadeep.openshop.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class VariantResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private int stockQuantity;
}
