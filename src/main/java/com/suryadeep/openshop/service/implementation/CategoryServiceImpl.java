package com.suryadeep.openshop.service.implementation;

import com.suryadeep.openshop.dto.request.CategoryRequest;
import com.suryadeep.openshop.dto.response.CategoryResponse;
import com.suryadeep.openshop.entity.Category;
import com.suryadeep.openshop.entity.Image;
import com.suryadeep.openshop.exception.CategoryNotFoundException;
import com.suryadeep.openshop.mapper.EntityMapper;
import com.suryadeep.openshop.repository.CategoryRepository;
import com.suryadeep.openshop.service.CategoryService;
import com.suryadeep.openshop.service.ImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityMapper entityMapper;
    private final ImageService imageService;

    @Override
    @Cacheable(value = "categories", key = "'allCategories'")
    public List<CategoryResponse> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(entityMapper::toCategoryResponse)
                .toList();
    }

    @Override
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getCategoryById(Long id) throws CategoryNotFoundException {
        log.info("Fetching category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        return entityMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", key = "'allCategories'")
    public CategoryResponse createCategory(CategoryRequest categoryRequest) throws IOException {
        log.info("Creating new category: {}", categoryRequest.getName());
        Category category = entityMapper.toCategoryEntity(categoryRequest);
        category = categoryRepository.save(category);

        if (categoryRequest.getImages() != null) {
            List<Image> images = imageService.saveImages(categoryRequest.getImages(), "categories");
            category.setImages(images);
            category = categoryRepository.save(category);
        }

        return entityMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "categories", key = "#categoryId"),
            @CacheEvict(value = "categories", key = "'allCategories'")
    })
    public CategoryResponse updateCategory(CategoryRequest categoryRequest, Long categoryId)
            throws CategoryNotFoundException, IOException {
        log.info("Updating category with ID: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        // Store old images for potential deletion
        List<Image> oldImages = new ArrayList<>(category.getImages());

        // Clear and update images
        category.getImages().clear();
        category = categoryRepository.save(category);

        if (categoryRequest.getImages() != null) {
            List<Image> newImages = imageService.saveImages(categoryRequest.getImages(), "categories");
            category.setImages(newImages);
            category = categoryRepository.save(category);
        }

        // Delete old images
        oldImages.forEach(image -> imageService.deleteImage(image.getId()));

        return entityMapper.toCategoryResponse(category);
    }


    @Override
    @Transactional
    @Caching(
        evict = {
            @CacheEvict(value = "categories", key = "#id"),
            @CacheEvict(value = "categories", key = "'allCategories'")
        }
    )
    public void deleteCategoryById(Long id) throws CategoryNotFoundException {
        log.info("Deleting category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        categoryRepository.delete(category);
    }

    @Override
    @Cacheable(value = "categories", key = "'page_' + #page + '_size_' + #size")
    public Page<CategoryResponse> findAllPaginated(int page, int size) {
        log.info("Fetching all categories with pagination - page: {}, size: {}", page, size);
        return categoryRepository.findAll(PageRequest.of(page, size))
                .map(entityMapper::toCategoryResponse);
    }
}
