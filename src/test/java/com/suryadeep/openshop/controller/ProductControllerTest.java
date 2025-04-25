package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.response.ProductResponse;
import com.suryadeep.openshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetAllProducts() {
        List<ProductResponse> productList = Arrays.asList(new ProductResponse(), new ProductResponse());
        Page<ProductResponse> productPage = new PageImpl<>(productList);

        when(productService.findAllPaginated(0, 10)).thenReturn(productPage);

        ResponseEntity<Object> response = productController.getProducts(0, 10, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productPage, response.getBody());
    }


    @Test
    void testGetProductById() {
        Long productId = 1L;
        ProductResponse productResponse = new ProductResponse();
        when(productService.getProduct(productId)).thenReturn(productResponse);

        ResponseEntity<ProductResponse> response = productController.getProduct(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productResponse, response.getBody());
    }

    @Test
    void testGetProductsByCategory() {
        Long categoryId = 1L;
        List<ProductResponse> products = Arrays.asList(new ProductResponse(), new ProductResponse());
        Page<ProductResponse> productPage = new PageImpl<>(products);
        when(productService.findByCategoryPaginated(categoryId, 0, 10)).thenReturn(productPage);

        ResponseEntity<Object> response = productController.getProducts(0, 10, categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productPage, response.getBody());
    }
}
