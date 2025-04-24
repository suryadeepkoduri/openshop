package com.suryadeep.openshop.controller;

import com.suryadeep.openshop.dto.response.CategoryResponse;
import com.suryadeep.openshop.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCategories() {
        CategoryResponse category1 = new CategoryResponse();
        category1.setId(1L);
        category1.setName("Category 1");

        CategoryResponse category2 = new CategoryResponse();
        category2.setId(2L);
        category2.setName("Category 2");

        List<CategoryResponse> categories = Arrays.asList(category1, category2);

        when(categoryService.findAllPaginated(0, 10)).thenReturn(categories);

        ResponseEntity<Object> response = categoryController.getAllCategories(0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
    }

    @Test
    public void testGetCategoryById() {
        CategoryResponse category = new CategoryResponse();
        category.setId(1L);
        category.setName("Category 1");

        when(categoryService.getCategoryById(1L)).thenReturn(category);

        ResponseEntity<CategoryResponse> response = categoryController.getCategoryById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(category, response.getBody());
    }
}
