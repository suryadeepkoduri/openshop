package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.ProductRequest;
import com.suryadeep.openshop.dto.response.ProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.util.List;

@Service
public interface ProductService {
    ProductResponse addProduct(ProductRequest productRequest);
    ProductResponse updateProduct(ProductRequest productRequest,Long productId);
    ProductResponse getProduct(Long productId);
    void deleteProduct(Long productId);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(Long categoryId);


    Page<ProductResponse> findAllPaginated(int page,int size);
    Page<ProductResponse> findByCategoryPaginated(Long categoryId, int page, int size);
}
