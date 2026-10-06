package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.book_info.request.CategoryRequest;
import com.example.library.dto.book_info.response.CategoryResponse;
import com.example.library.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ApiResponse<CategoryResponse> createCategory(
            @RequestBody @Valid CategoryRequest request
    ) {
        return ApiResponse.success(
                categoryService.createCategory(request)
        );
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ApiResponse<CategoryResponse> restoreCategory(
            @PathVariable String id,
            @RequestBody @Valid CategoryRequest request
    ) {
        return ApiResponse.success(
                categoryService.updateCategory(id, request, true)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ApiResponse<CategoryResponse> updateCategory(
            @PathVariable String id,
            @RequestBody @Valid CategoryRequest request
    ) {
        return ApiResponse.success(
                categoryService.updateCategory(id, request, false)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    public ApiResponse<CategoryResponse> deleteCategory(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                categoryService.deleteCategory(id)
        );
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategories() {
        return ApiResponse.success(categoryService.getCategories());
    }
}