package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.category_author_publisher.request.CategoryRequest;
import com.example.library.dto.category_author_publisher.response.CategoryResponse;
import com.example.library.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(
            @RequestBody @Valid CategoryRequest request
    ) {
        return ApiResponse.success(
                categoryService.createCategory(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable String id,
            @RequestBody @Valid CategoryRequest request
    ) {
        return ApiResponse.success(
                categoryService.updateCategory(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<CategoryResponse> deleteCategory(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                categoryService.deleteCategory(id)
        );
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategories() {
        return ApiResponse.success(
                categoryService.getCategories()
        );
    }
}