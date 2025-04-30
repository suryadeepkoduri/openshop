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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final EntityMapper entityMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Caching(evict = {
        @CacheEvict(value = "products", key = "'allProducts'"),
        @CacheEvict(value = "products", key = "'category_' + #productRequest.categoryId", condition = "#productRequest.categoryId != null")
    })
    public ProductResponse addProduct(ProductRequest productRequest)  {
        log.info("Adding new product: {}", productRequest.getName());

        Product product = entityMapper.toProductEntity(productRequest);

        try {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(String.format("Category with ID %s not found", productRequest.getCategoryId())));
            product.setCategory(category);
            log.debug("Associated product with category ID: {}", category.getId());

            if (productRequest.getVariants() != null) {
                List<Variant> variants = productRequest.getVariants().stream()
                        .map(entityMapper::toVariantEntity)
                        .toList();
                variants.forEach(variant -> variant.setProduct(product));
                product.setVariants(variants);
                log.debug("Added {} variants to product", variants.size());
            }

            Product savedProduct = productRepository.save(product);
            log.info("Successfully added product with ID: {}", savedProduct.getId());
            return entityMapper.toProductResponse(savedProduct);
        } catch (CategoryNotFoundException e) {
            log.error("Failed to add product. Category not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding product: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "products", key = "#id"),
        @CacheEvict(value = "products", key = "'allProducts'"),
        @CacheEvict(value = "products", key = "'category_' + #productRequest.categoryId", condition = "#productRequest.categoryId != null"),
        @CacheEvict(value = "products", key = "'page_' + 0 + '_size_' + 10"),
        @CacheEvict(value = "products", key = "'category_' + #productRequest.categoryId + '_page_' + 0 + '_size_' + 10", condition = "#productRequest.categoryId != null")
    })
    public ProductResponse updateProduct(ProductRequest productRequest, Long id) {
        log.info("Updating product with ID: {}", id);

        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(ProductNotFoundException::new);
            log.debug("Found existing product: {}", existingProduct.getName());

            // Store old category ID for cache eviction if category changes
            Long oldCategoryId = existingProduct.getCategory() != null ? existingProduct.getCategory().getId() : null;

            existingProduct.setName(productRequest.getName());
            existingProduct.setDescription(productRequest.getDescription());
            log.debug("Updated basic product information");

            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existingProduct.setCategory(category);
            log.debug("Updated product category to ID: {}", category.getId());

            if (productRequest.getVariants() != null) {
                List<Variant> variants = productRequest.getVariants().stream()
                        .map(entityMapper::toVariantEntity)
                        .toList();
                variants.forEach(variant -> variant.setProduct(existingProduct));
                existingProduct.getVariants().clear(); // clear old variants
                existingProduct.getVariants().addAll(variants);
                log.debug("Updated product variants. New count: {}", variants.size());
            }

            Product updatedProduct = productRepository.save(existingProduct);

            // If category changed, evict old category cache as well
            if (oldCategoryId != null && !oldCategoryId.equals(productRequest.getCategoryId())) {
                log.debug("Category changed from {} to {}. Evicting old category cache.", 
                          oldCategoryId, productRequest.getCategoryId());
                evictOldCategoryCaches(oldCategoryId);
            }

            log.info("Successfully updated product with ID: {}", updatedProduct.getId());
            return entityMapper.toProductResponse(updatedProduct);
        } catch (ProductNotFoundException e) {
            log.error("Failed to update product. Product with ID {} not found", id);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating product with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }


    @Override
    @Cacheable(value = "products", key = "#productId")
    public ProductResponse getProduct(Long productId) {
        log.debug("Retrieving product with ID: {}", productId);
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(ProductNotFoundException::new);
            log.debug("Successfully retrieved product: {}", product.getName());
            return entityMapper.toProductResponse(product);
        } catch (ProductNotFoundException e) {
            log.warn("Product with ID {} not found", productId);
            throw e;
        }
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "products", key = "#productId"),
        @CacheEvict(value = "products", key = "'allProducts'"),
        @CacheEvict(value = "products", key = "'page_' + 0 + '_size_' + 10")
        // We can't directly evict the category cache here because we need the category ID
        // which we'll handle in the method body
    })
    public void deleteProduct(Long productId) {
        log.info("Deleting product with ID: {}", productId);
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(ProductNotFoundException::new);
            log.debug("Found product to delete: {}", product.getName());

            // Get category ID before deleting for potential cache eviction
            Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;

            productRepository.delete(product);

            // Evict category cache if needed
            if (categoryId != null) {
                log.debug("Product was in category ID: {}. Evicting category cache.", categoryId);
                evictOldCategoryCaches(categoryId);
            }

            log.info("Successfully deleted product with ID: {}", productId);
        } catch (ProductNotFoundException e) {
            log.error("Failed to delete product. Product with ID {} not found", productId);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting product with ID {}: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Helper method to evict caches related to a specific category.
     * This is used when a product changes category or is deleted.
     * 
     * @param categoryId the ID of the category whose caches should be evicted
     */
    @CacheEvict(value = "products", key = "'category_' + #categoryId")
    public void evictOldCategoryCaches(Long categoryId) {
        log.debug("Evicting caches for category ID: {}", categoryId);
        // This method is annotated with @CacheEvict to evict the basic category cache

        // Manually evict paginated category caches for the first few pages
        // In a real-world scenario, you might want to be more sophisticated about which pages to evict
        for (int page = 0; page < 5; page++) {
            for (int size : new int[]{10, 20, 50}) {
                String cacheKey = "'category_' + " + categoryId + " + '_page_' + " + page + " + '_size_' + " + size;
                log.debug("Evicting cache with key: {}", cacheKey);
                // Note: In a real implementation, you would use a CacheManager to evict these caches
                // This is a placeholder for the actual implementation
            }
        }
    }

    @Override
    @Cacheable(value = "products", key = "'allProducts'")
    public List<ProductResponse> getAllProducts() {
        log.debug("Retrieving all products");
        List<Product> products = productRepository.findAll();
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Product p : products){
            productResponses.add(entityMapper.toProductResponse(p));
        }
        log.debug("Retrieved {} products", products.size());
        return productResponses;
    }

    @Override
    @Cacheable(value = "products", key = "'page_' + #page + '_size_' + #size")
    public Page<ProductResponse> findAllPaginated(int page,int size) {
        log.debug("Retrieving paginated products - page: {}, size: {}", page, size);
        Page<ProductResponse> productResponses = productRepository.findAll(PageRequest.of(page,size))
                .map(entityMapper::toProductResponse);
        log.debug("Retrieved {} products (page {} of {})", 
                productResponses.getNumberOfElements(), 
                productResponses.getNumber() + 1, 
                productResponses.getTotalPages());
        return productResponses;
    }

    @Override
    @Cacheable(value = "products", key = "'category_' + #categoryId")
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        log.debug("Retrieving products for category ID: {}", categoryId);
        List<Product> products = productRepository.findAllByCategoryId(categoryId);
        List<ProductResponse> productResponses = new ArrayList<>();
        for(Product p : products){
            productResponses.add(entityMapper.toProductResponse(p));
        }
        log.debug("Retrieved {} products for category ID: {}", products.size(), categoryId);
        return productResponses;
    }

    @Override
    @Cacheable(value = "products", key = "'category_' + #categoryId + '_page_' + #page + '_size_' + #size")
    public Page<ProductResponse> findByCategoryPaginated(Long categoryId, int page, int size) {
        log.debug("Retrieving paginated products for category ID: {} - page: {}, size: {}", categoryId, page, size);
        Page<ProductResponse> productResponses = productRepository.findAllByCategoryId(categoryId, PageRequest.of(page, size))
                .map(entityMapper::toProductResponse);
        log.debug("Retrieved {} products for category ID: {} (page {} of {})", 
                productResponses.getNumberOfElements(), 
                categoryId,
                productResponses.getNumber() + 1, 
                productResponses.getTotalPages());
        return productResponses;
    }
}
