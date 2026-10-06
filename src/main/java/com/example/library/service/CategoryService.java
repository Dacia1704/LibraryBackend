package com.example.library.service;


import com.example.library.dto.book_info.request.CategoryRequest;
import com.example.library.dto.book_info.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(String id,CategoryRequest request, Boolean isRestore);

    CategoryResponse deleteCategory(String id);

    List<CategoryResponse> getCategories();
}