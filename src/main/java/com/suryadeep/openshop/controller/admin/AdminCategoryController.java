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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoryResponse> addCategory(
            @Parameter(description = "Category details", required = true)
            @RequestPart("categoryRequest") CategoryRequest categoryRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        log.info("Adding new category: {}", categoryRequest.getName());

        categoryRequest.setImages(images);

        // Create category and return response
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
    @PutMapping( path = "/{categoryId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "Updated category details", required = true)
            @RequestPart("categoryRequest") CategoryRequest categoryRequest,
            @Parameter(description = "ID of the category to update", required = true)
            @PathVariable Long categoryId,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        log.info("Updating category with ID: {}", categoryId);
        categoryRequest.setImages(images);

        // Update the category and return the response
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
        log.info("Deleting category with ID: {}", categoryId);
        categoryService.deleteCategoryById(categoryId);
        return new ResponseEntity<>("Category deleted successfully", HttpStatus.NO_CONTENT);
    }
}
