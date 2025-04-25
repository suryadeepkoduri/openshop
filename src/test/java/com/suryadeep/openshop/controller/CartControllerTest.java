package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.request.CartItemRequest;
import com.suryadeep.openshop.dto.response.CartItemResponse;
import com.suryadeep.openshop.dto.response.CartResponse;
import com.suryadeep.openshop.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCart() {
        CartResponse cartResponse = new CartResponse();
        when(cartService.getCart()).thenReturn(cartResponse);

        ResponseEntity<Object> response = cartController.getCart();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cartResponse, response.getBody());
    }

    @Test
    public void testAddCartItem() {
        CartItemRequest cartItemRequest = new CartItemRequest();
        when(cartService.addItemToCart(any(CartItemRequest.class))).thenReturn(new CartItemResponse());

        ResponseEntity<Object> response = cartController.addCartItem(cartItemRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartService, times(1)).addItemToCart(any(CartItemRequest.class));
    }

    @Test
    public void testUpdateCartItem() {
        Long cartItemId = 1L;
        CartItemRequest cartItemRequest = new CartItemRequest();
        when(cartService.updateItemInCart(any(CartItemRequest.class), eq(cartItemId))).thenReturn(new CartItemResponse());

        ResponseEntity<Object> response = cartController.updateCartItem(cartItemRequest, cartItemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartService, times(1)).updateItemInCart(any(CartItemRequest.class), eq(cartItemId));
    }

    @Test
    public void testRemoveCartItem() {
        Long cartItemId = 1L;
        doNothing().when(cartService).removeItemFromCart(cartItemId);

        ResponseEntity<Object> response = cartController.removeCartItem(cartItemId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService, times(1)).removeItemFromCart(cartItemId);
    }
}
