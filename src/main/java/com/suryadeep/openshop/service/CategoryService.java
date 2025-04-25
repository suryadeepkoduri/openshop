package com.suryadeep.openshop.service;

import com.suryadeep.openshop.dto.request.CategoryRequest;
import com.suryadeep.openshop.dto.response.CategoryResponse;
import com.suryadeep.openshop.exception.CategoryNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id) throws CategoryNotFoundException;
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    CategoryResponse updateCategory(CategoryRequest categoryRequest,Long categoryId) throws CategoryNotFoundException;
    void deleteCategoryById(Long id) throws CategoryNotFoundException;

    Page<CategoryResponse> findAllPaginated(int page, int size);
}
