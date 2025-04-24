package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.response.ProductResponse;
import com.suryadeep.openshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        List<ProductResponse> products = Arrays.asList(new ProductResponse(), new ProductResponse());
        when(productService.getAllProducts()).thenReturn(products);

        ResponseEntity<Object> response = productController.getProducts(0, 10, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }

    @Test
    public void testGetProductById() {
        Long productId = 1L;
        ProductResponse productResponse = new ProductResponse();
        when(productService.getProduct(productId)).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productResponse, response.getBody());
    }

    @Test
    public void testGetProductsByCategory() {
        Long categoryId = 1L;
        List<ProductResponse> products = Arrays.asList(new ProductResponse(), new ProductResponse());
        when(productService.getProductsByCategory(categoryId)).thenReturn(products);

        ResponseEntity<Object> response = productController.getProducts(0, 10, categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }

    @Test
    public void testSearchProducts() {
        String query = "laptop";
        List<ProductResponse> products = Arrays.asList(new ProductResponse(), new ProductResponse());
        when(productService.searchProducts(query)).thenReturn(products);

        ResponseEntity<Object> response = productController.searchProducts(query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
    }
}
