package com.suryadeep.openshop.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartItemRequest {
    private Long variantId;
    @Min(value = 1, message = "Quantity should be greater than 0")
    private int quantity;
}
