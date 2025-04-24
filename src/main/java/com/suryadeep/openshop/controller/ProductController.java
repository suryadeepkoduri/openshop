package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.response.ProductResponse;
import com.suryadeep.openshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;

    @Operation(
        summary = "Get all products",
        description = "Returns a paginated list of products, optionally filtered by category"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    public ResponseEntity<Object> getProducts(
        @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Category ID to filter products") @RequestParam(required = false) Long categoryId) {

    if (categoryId != null) {
        return new ResponseEntity<>(productService.findByCategoryPaginated(categoryId, page, size), HttpStatus.OK);
    }
    return new ResponseEntity<>(productService.findAllPaginated(page, size), HttpStatus.OK);
}

    @Operation(
        summary = "Get product by ID",
        description = "Returns a single product by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(
        @Parameter(description = "ID of the product to retrieve", required = true) 
        @PathVariable Long productId) {
        return new ResponseEntity<>(productService.getProduct(productId), HttpStatus.OK);
    }
    //TODO Add Image to product
    //TODO Configure refresh token in jwt service
    //TODO Implement order functionality
}
