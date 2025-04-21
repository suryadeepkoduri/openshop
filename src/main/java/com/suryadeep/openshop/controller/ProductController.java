package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.response.ProductResponse;
import com.suryadeep.openshop.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<Object> getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Long categoryId) {
    
    if (categoryId != null) {
        return new ResponseEntity<>(productService.findByCategoryPaginated(categoryId, page, size), HttpStatus.OK);
    }
    return new ResponseEntity<>(productService.findAllPaginated(page, size), HttpStatus.OK);
}

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.getProduct(productId), HttpStatus.OK);
    }

}