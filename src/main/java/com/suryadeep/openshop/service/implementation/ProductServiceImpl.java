package com.suryadeep.openshop.service.implementation;

import com.suryadeep.openshop.dto.request.ProductRequest;
import com.suryadeep.openshop.dto.response.ProductResponse;
import com.suryadeep.openshop.entity.Category;
import com.suryadeep.openshop.entity.Product;
import com.suryadeep.openshop.entity.Variant;
import com.suryadeep.openshop.exception.CategoryNotFoundException;
import com.suryadeep.openshop.exception.ProductNotFoundException;
import com.suryadeep.openshop.mapper.EntityMapper;
import com.suryadeep.openshop.repository.CategoryRepository;
import com.suryadeep.openshop.repository.ProductRepository;
import com.suryadeep.openshop.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final EntityMapper entityMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse addProduct(ProductRequest productRequest)  {
        logger.info("Adding new product: {}", productRequest.getName());

        Product product = entityMapper.toProductEntity(productRequest);

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with ID %s not found", productRequest.getCategoryId())));
        product.setCategory(category);

        if (productRequest.getVariants() != null) {
            List<Variant> variants = productRequest.getVariants().stream()
                    .map(entityMapper::toVariantEntity)
                    .peek(variant -> variant.setProduct(product))
                    .toList();
            product.setVariants(variants);
        }

        Product savedProduct = productRepository.save(product);
        logger.info("Product added successfully with ID: {}", savedProduct.getId());
        return entityMapper.toProductResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(ProductRequest productRequest, Long id) {
        logger.info("Updating product with ID: {}", id);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        existingProduct.setName(productRequest.getName());
        existingProduct.setDescription(productRequest.getDescription());

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existingProduct.setCategory(category);

        if (productRequest.getVariants() != null) {
            List<Variant> variants = productRequest.getVariants().stream()
                    .map(entityMapper::toVariantEntity)
                    .peek(variant -> variant.setProduct(existingProduct))
                    .toList();
            existingProduct.getVariants().clear(); // clear old variants
            existingProduct.getVariants().addAll(variants);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        logger.info("Product updated successfully with ID: {}", updatedProduct.getId());
        return entityMapper.toProductResponse(updatedProduct);
    }


    @Override
    public ProductResponse getProduct(Long productId) {
        logger.info("Fetching product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
        return entityMapper.toProductResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        logger.info("Deleting product with ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);
        productRepository.delete(product);
        logger.info("Product deleted successfully with ID: {}", productId);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Product p : products){
            productResponses.add(entityMapper.toProductResponse(p));
        }
        return productResponses;
    }

    public Page<ProductResponse> findAllPaginated(int page,int size) {
        logger.info("Fetching all products with pagination - page: {}, size: {}", page, size);
        return productRepository.findAll(PageRequest.of(page,size))
                .map(entityMapper::toProductResponse);
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        logger.info("Fetching products by category ID: {}", categoryId);
        List<Product> products = productRepository.findAllByCategoryId(categoryId);
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Product p : products){
            productResponses.add(entityMapper.toProductResponse(p));
        }
        return productResponses;
    }

    @Override
    public Page<ProductResponse> findByCategoryPaginated(Long categoryId, int page, int size) {
        logger.info("Fetching products by category ID: {} with pagination - page: {}, size: {}", categoryId, page, size);
        return productRepository.findAllByCategoryId(categoryId, PageRequest.of(page, size))
                .map(entityMapper::toProductResponse);
    }
}
