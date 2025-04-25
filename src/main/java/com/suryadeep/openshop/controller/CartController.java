package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.request.CartItemRequest;
import com.suryadeep.openshop.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Shopping cart management APIs")
public class CartController {

    private final CartService cartService;

    @Operation(
        summary = "Get user's cart",
        description = "Returns the current user's shopping cart with all items"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved cart"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    public ResponseEntity<Object> getCart() {
        log.info("Fetching user's cart");
        return ResponseEntity.ok(cartService.getCart());
    }

    @Operation(
        summary = "Add item to cart",
        description = "Adds a new item to the user's shopping cart"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item successfully added to cart"),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Product or variant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/items")
    public ResponseEntity<Object> addCartItem(
        @Parameter(description = "Cart item details", required = true) 
        @RequestBody CartItemRequest cartItemRequest) {
        log.info("Adding item to cart: {}", cartItemRequest.getVariantId());
        return ResponseEntity.ok(cartService.addItemToCart(cartItemRequest));
    }

    @Operation(
        summary = "Update cart item",
        description = "Updates an existing item in the user's shopping cart"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Cart item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Object> updateCartItem(
        @Parameter(description = "Updated cart item details", required = true) 
        @RequestBody CartItemRequest cartItemRequest,
        @Parameter(description = "ID of the cart item to update", required = true) 
        @PathVariable Long cartItemId) {
        log.info("Updating cart item with ID: {}", cartItemId);
        return ResponseEntity.ok(cartService.updateItemInCart(cartItemRequest, cartItemId));
    }

    @Operation(
        summary = "Remove cart item",
        description = "Removes an item from the user's shopping cart"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Item successfully removed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "404", description = "Cart item not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Object> removeCartItem(
        @Parameter(description = "ID of the cart item to remove", required = true) 
        @PathVariable Long cartItemId) {
        log.info("Removing cart item with ID: {}", cartItemId);
        cartService.removeItemFromCart(cartItemId);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.NO_CONTENT);
    }

}
