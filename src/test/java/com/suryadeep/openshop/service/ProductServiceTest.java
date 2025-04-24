package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.ProductRequest;
import com.suryadeep.openshop.dto.response.ProductResponse;
import com.suryadeep.openshop.entity.Category;
import com.suryadeep.openshop.entity.Product;
import com.suryadeep.openshop.entity.Variant;
import com.suryadeep.openshop.exception.ProductNotFoundException;
import com.suryadeep.openshop.mapper.EntityMapper;
import com.suryadeep.openshop.repository.CategoryRepository;
import com.suryadeep.openshop.repository.ProductRepository;
import com.suryadeep.openshop.service.implementation.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProduct() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Laptop");
        productRequest.setDescription("Gaming Laptop");
        productRequest.setCategoryId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setDescription("Gaming Laptop");

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(entityMapper.toProductEntity(any(ProductRequest.class))).thenReturn(product);
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(entityMapper.toProductResponse(any(Product.class))).thenReturn(new ProductResponse());

        ProductResponse productResponse = productService.addProduct(productRequest);

        assertNotNull(productResponse);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct() {
        Long productId = 1L;
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Laptop");
        productRequest.setDescription("Gaming Laptop");
        productRequest.setCategoryId(1L);

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Laptop");
        existingProduct.setDescription("Old Description");

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);
        when(entityMapper.toProductResponse(any(Product.class))).thenReturn(new ProductResponse());

        ProductResponse productResponse = productService.updateProduct(productRequest, productId);

        assertNotNull(productResponse);
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.deleteProduct(productId);

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).delete(any(Product.class));
    }

    @Test
    void testFindAllPaginated() {
        int page = 0;
        int size = 10;
        List<Product> products = List.of(new Product(), new Product());
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findAll(PageRequest.of(page, size))).thenReturn(productPage);
        when(entityMapper.toProductResponse(any(Product.class))).thenReturn(new ProductResponse());

        Page<ProductResponse> productResponses = productService.findAllPaginated(page, size);

        assertNotNull(productResponses);
        assertEquals(2, productResponses.getTotalElements());
        verify(productRepository, times(1)).findAll(PageRequest.of(page, size));
    }

    @Test
    void testGetProductById() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(entityMapper.toProductResponse(any(Product.class))).thenReturn(new ProductResponse());

        ProductResponse productResponse = productService.getProduct(productId);

        assertNotNull(productResponse);
        verify(productRepository, times(1)).findById(productId);
    }
}
