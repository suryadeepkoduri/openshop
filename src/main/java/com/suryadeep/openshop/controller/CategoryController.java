package com.suryadeep.openshop.controller;

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
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Product category management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
        summary = "Get all categories",
        description = "Returns a paginated list of product categories"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved categories"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    public ResponseEntity<Object> getAllCategories(
            @Parameter(description = "Page number (zero-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(categoryService.findAllPaginated(page,size), HttpStatus.OK);
    }

    @Operation(
        summary = "Get category by ID",
        description = "Returns a single category by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved category",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = CategoryResponse.class))),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(
        @Parameter(description = "ID of the category to retrieve", required = true) 
        @PathVariable Long categoryId) {
        return new ResponseEntity<>(categoryService.getCategoryById(categoryId),HttpStatus.OK);
    }
}
