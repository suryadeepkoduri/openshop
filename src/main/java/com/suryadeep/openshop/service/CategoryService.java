package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.CategoryRequest;
import com.suryadeep.openshop.dto.response.CategoryResponse;
import com.suryadeep.openshop.exception.CategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    public List<CategoryResponse> getAllCategories();
    public CategoryResponse getCategoryById(Long id) throws CategoryNotFoundException;
    public CategoryResponse createCategory(CategoryRequest categoryRequest);
    public CategoryResponse updateCategory(CategoryRequest categoryRequest,Long categoryId) throws CategoryNotFoundException;
    public void deleteCategoryById(Long id) throws CategoryNotFoundException;
}
