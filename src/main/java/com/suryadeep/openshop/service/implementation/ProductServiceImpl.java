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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final EntityMapper entityMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
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
                        .peek(variant -> variant.setProduct(product))
                        .toList();
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
    public ProductResponse updateProduct(ProductRequest productRequest, Long id) {
        log.info("Updating product with ID: {}", id);

        try {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(ProductNotFoundException::new);
            log.debug("Found existing product: {}", existingProduct.getName());

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
                        .peek(variant -> variant.setProduct(existingProduct))
                        .toList();
                existingProduct.getVariants().clear(); // clear old variants
                existingProduct.getVariants().addAll(variants);
                log.debug("Updated product variants. New count: {}", variants.size());
            }

            Product updatedProduct = productRepository.save(existingProduct);
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
    public void deleteProduct(Long productId) {
        log.info("Deleting product with ID: {}", productId);
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(ProductNotFoundException::new);
            log.debug("Found product to delete: {}", product.getName());
            productRepository.delete(product);
            log.info("Successfully deleted product with ID: {}", productId);
        } catch (ProductNotFoundException e) {
            log.error("Failed to delete product. Product with ID {} not found", productId);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting product with ID {}: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
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
