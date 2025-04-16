package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.ProductRequest;
import com.suryadeep.openshop.dto.response.ProductResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    public ProductResponse addProduct(ProductRequest productRequest);
    public ProductResponse updateProduct(ProductRequest productRequest,Long productId);
    public ProductResponse getProduct(Long productId);
    public void deleteProduct(Long productId);
    public List<ProductResponse> getAllProducts();
    public List<ProductResponse> getProductsByCategory(Long categoryId);
}
