package com.suryadeep.openshop.controller.admin;

import com.suryadeep.openshop.dto.request.CategoryRequest;
import com.suryadeep.openshop.dto.response.CategoryResponse;
import com.suryadeep.openshop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "Admin - Categories", description = "Category management APIs for administrators")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Operation(
        summary = "Create new category",
        description = "Creates a new product category (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category successfully created",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("")
    public ResponseEntity<CategoryResponse> addCategory(
        @Parameter(description = "Category details", required = true) 
        @RequestBody CategoryRequest categoryRequest) {
        return new ResponseEntity<>(categoryService.createCategory(categoryRequest), HttpStatus.CREATED);
    }

    @Operation(
        summary = "Update category",
        description = "Updates an existing product category (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category successfully updated",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request - Bad input parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
        @Parameter(description = "Updated category details", required = true) 
        @RequestBody CategoryRequest categoryRequest, 
        @Parameter(description = "ID of the category to update", required = true) 
        @PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.updateCategory(categoryRequest, categoryId), HttpStatus.OK);
    }

    @Operation(
        summary = "Delete category",
        description = "Deletes an existing product category (Admin only)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Category successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized as admin"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "409", description = "Conflict - Category has associated products"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(
        @Parameter(description = "ID of the category to delete", required = true) 
        @PathVariable Long categoryId) {
        categoryService.deleteCategoryById(categoryId);
        return new ResponseEntity<>("Category deleted successfully", HttpStatus.NO_CONTENT);
    }
}
