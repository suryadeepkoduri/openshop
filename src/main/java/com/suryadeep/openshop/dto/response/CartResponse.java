package com.suryadeep.openshop.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CartResponse {
    private List<CartItemResponse> cartItems;
    private double price;
}
