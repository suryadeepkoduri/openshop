package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.CategoryRequest;
import com.suryadeep.openshop.dto.response.CategoryResponse;
import com.suryadeep.openshop.entity.Category;
import com.suryadeep.openshop.exception.CategoryNotFoundException;
import com.suryadeep.openshop.mapper.EntityMapper;
import com.suryadeep.openshop.repository.CategoryRepository;
import com.suryadeep.openshop.service.implementation.CategoryServiceImpl;
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

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCategory() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Electronics");
        categoryRequest.setDescription("Electronic items");

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setDescription("Electronic items");

        when(entityMapper.toCategoryEntity(any(CategoryRequest.class))).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(entityMapper.toCategoryResponse(any(Category.class))).thenReturn(new CategoryResponse());

        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);

        assertNotNull(categoryResponse);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testUpdateCategory() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Electronics");
        categoryRequest.setDescription("Electronic items");

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName("Old Name");
        existingCategory.setDescription("Old Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);
        when(entityMapper.toCategoryResponse(any(Category.class))).thenReturn(new CategoryResponse());

        CategoryResponse categoryResponse = categoryService.updateCategory(categoryRequest, categoryId);

        assertNotNull(categoryResponse);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testDeleteCategoryById() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategoryById(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).delete(any(Category.class));
    }

    @Test
    void testFindAllPaginated() {
        int page = 0;
        int size = 10;
        List<Category> categories = List.of(new Category(), new Category());
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findAll(PageRequest.of(page, size))).thenReturn(categoryPage);
        when(entityMapper.toCategoryResponse(any(Category.class))).thenReturn(new CategoryResponse());

        Page<CategoryResponse> categoryResponses = categoryService.findAllPaginated(page, size);

        assertNotNull(categoryResponses);
        assertEquals(2, categoryResponses.getTotalElements());
        verify(categoryRepository, times(1)).findAll(PageRequest.of(page, size));
    }

    @Test
    void testGetCategoryById() {
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(entityMapper.toCategoryResponse(any(Category.class))).thenReturn(new CategoryResponse());

        CategoryResponse categoryResponse = categoryService.getCategoryById(categoryId);

        assertNotNull(categoryResponse);
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategoryByIdThrowsCategoryNotFoundException() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(categoryId));

        verify(categoryRepository, times(1)).findById(categoryId);
    }
}
