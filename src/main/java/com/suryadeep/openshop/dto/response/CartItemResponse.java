package com.suryadeep.openshop.dto.response;

import lombok.Data;

@Data
public class CartItemResponse {
    Long id;
    Long variantId;
    Long quantity;
}
