package com.suryadeep.openshop.service.implementation;

import com.suryadeep.openshop.dto.request.CategoryRequest;
import com.suryadeep.openshop.dto.response.CategoryResponse;
import com.suryadeep.openshop.entity.Category;
import com.suryadeep.openshop.exception.CategoryNotFoundException;
import com.suryadeep.openshop.mapper.EntityMapper;
import com.suryadeep.openshop.repository.CategoryRepository;
import com.suryadeep.openshop.service.CategoryService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final EntityMapper entityMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        logger.info("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(entityMapper::toCategoryResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) throws CategoryNotFoundException {
        logger.info("Fetching category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        return entityMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        logger.info("Creating new category: {}", categoryRequest.getName());
        Category category = entityMapper.toCategoryEntity(categoryRequest);
        category = categoryRepository.save(category);
        return entityMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(CategoryRequest categoryRequest, Long categoryId) throws CategoryNotFoundException {
        logger.info("Updating category with ID: {}", categoryId);
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);
        existingCategory.setName(categoryRequest.getName());
        existingCategory.setDescription(categoryRequest.getDescription());
        Category updatedCategory = categoryRepository.save(existingCategory);
        return entityMapper.toCategoryResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long id) throws CategoryNotFoundException {
        logger.info("Deleting category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
        categoryRepository.delete(category);
    }

    @Override
    public Page<CategoryResponse> findAllPaginated(int page, int size) {
        logger.info("Fetching all categories with pagination - page: {}, size: {}", page, size);
        return categoryRepository.findAll(PageRequest.of(page, size))
                .map(entityMapper::toCategoryResponse);
    }
}
