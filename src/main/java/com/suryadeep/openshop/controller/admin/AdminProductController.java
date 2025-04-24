package com.suryadeep.openshop.controller.admin;

import com.suryadeep.openshop.dto.request.ProductRequest;
import com.suryadeep.openshop.dto.response.ProductResponse;
import com.suryadeep.openshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "Admin - Products", description = "Product management APIs for administrators")
public class AdminProductController {
    private final ProductService productService;

    @Operation(
        summary = "Create new product",
        description = "Creates a new product with variants (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product successfully created",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("")
    public ResponseEntity<ProductResponse> addProduct(
        @Parameter(description = "Product details including variants", required = true) 
        @Valid @RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>(productService.addProduct(productRequest), HttpStatus.CREATED);
    }


    @Operation(
        summary = "Update product",
        description = "Updates an existing product and its variants (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product successfully updated",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ProductResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
        @Parameter(description = "ID of the product to update", required = true) 
        @PathVariable Long productId,
        @Parameter(description = "Updated product details", required = true) 
        @Valid @RequestBody ProductRequest productRequest) {
        return new ResponseEntity<>(productService.updateProduct(productRequest,productId),HttpStatus.OK);
    }

    @Operation(
        summary = "Delete product",
        description = "Deletes an existing product and its variants (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "409", description = "Conflict - Product has associated orders"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(
        @Parameter(description = "ID of the product to delete", required = true) 
        @PathVariable Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.NO_CONTENT);
    }


}
