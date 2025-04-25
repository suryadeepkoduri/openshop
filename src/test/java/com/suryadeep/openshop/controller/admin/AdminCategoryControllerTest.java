package com.suryadeep.openshop.controller.admin;

import com.suryadeep.openshop.dto.request.CategoryRequest;
import com.suryadeep.openshop.dto.response.CategoryResponse;
import com.suryadeep.openshop.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminCategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private AdminCategoryController adminCategoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCategory() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Electronics");
        categoryRequest.setDescription("Electronic items");

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Electronics");
        categoryResponse.setDescription("Electronic items");

        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(categoryResponse);

        ResponseEntity<CategoryResponse> response = adminCategoryController.addCategory(categoryRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(categoryResponse, response.getBody());
        verify(categoryService, times(1)).createCategory(any(CategoryRequest.class));
    }

    @Test
    void testUpdateCategory() {
        Long categoryId = 1L;
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName("Electronics");
        categoryRequest.setDescription("Electronic items");

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(categoryId);
        categoryResponse.setName("Electronics");
        categoryResponse.setDescription("Electronic items");

        when(categoryService.updateCategory(any(CategoryRequest.class), eq(categoryId))).thenReturn(categoryResponse);

        ResponseEntity<CategoryResponse> response = adminCategoryController.updateCategory(categoryRequest, categoryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryResponse, response.getBody());
        verify(categoryService, times(1)).updateCategory(any(CategoryRequest.class), eq(categoryId));
    }

    @Test
    void testDeleteCategory() {
        Long categoryId = 1L;

        doNothing().when(categoryService).deleteCategoryById(categoryId);

        ResponseEntity<String> response = adminCategoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Category deleted successfully", response.getBody());
        verify(categoryService, times(1)).deleteCategoryById(categoryId);
    }
}
