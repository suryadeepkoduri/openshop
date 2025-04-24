package com.suryadeep.openshop.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suryadeep.openshop.controller.GlobalExceptionHandler;
import com.suryadeep.openshop.dto.request.ProductRequest;
import com.suryadeep.openshop.dto.request.VariantRequest;
import com.suryadeep.openshop.dto.response.ProductResponse;
import com.suryadeep.openshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private AdminProductController adminProductController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders
       .standaloneSetup(adminProductController)
       .setControllerAdvice(new GlobalExceptionHandler()) // Add any exception handlers
       .setValidator(new LocalValidatorFactoryBean()) // Add validator for @Valid
       .build();
    }

    @Test
    public void testAddProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        ProductResponse productResponse = new ProductResponse();

        productRequest.setName("Test Product");
        productRequest.setDescription("Test Product Description");
        productRequest.setCategoryId(1L);
        productRequest.setVariants(new ArrayList<VariantRequest>());

        when(productService.addProduct(any(ProductRequest.class))).thenReturn(productResponse);

        mockMvc.perform(post("/api/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateProduct() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        ProductResponse productResponse = new ProductResponse();

        productRequest.setName("Test Product");
        productRequest.setDescription("Test Product Description");
        productRequest.setCategoryId(1L);
        productRequest.setVariants(new ArrayList<VariantRequest>());

        when(productService.updateProduct(any(ProductRequest.class), anyLong())).thenReturn(productResponse);

        mockMvc.perform(put("/api/admin/products/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(productRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/admin/products/{productId}", 1L))
                .andExpect(status().isNoContent());
    }
}