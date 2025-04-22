package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.request.CartItemRequest;
import com.suryadeep.openshop.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("")
    public ResponseEntity<Object> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }
    
    @PostMapping("/items")
    public ResponseEntity<Object> addCartItem(@RequestBody CartItemRequest cartItemRequest) {
        return ResponseEntity.ok(cartService.addItemToCart(cartItemRequest));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Object> updateCartItem(@RequestBody CartItemRequest cartItemRequest,@PathVariable Long cartItemId){
        return ResponseEntity.ok(cartService.updateItemInCart(cartItemRequest,cartItemId));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Object> removeCartItem(@PathVariable Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.NO_CONTENT);
    }

}
