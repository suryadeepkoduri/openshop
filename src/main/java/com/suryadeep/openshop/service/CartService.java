package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.CartItemRequest;
import com.suryadeep.openshop.dto.response.CartItemResponse;
import com.suryadeep.openshop.dto.response.CartResponse;


public interface CartService {
    CartResponse getCart();
    CartItemResponse addItemToCart(CartItemRequest cartItemRequest);
    void removeItemFromCart(Long cartItemId);
    CartItemResponse updateItemInCart(CartItemRequest cartItemRequest,Long cartItemId);
}
