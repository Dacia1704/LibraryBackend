package com.example.library.service;


import com.example.library.dto.category_author_publisher.request.CategoryRequest;
import com.example.library.dto.category_author_publisher.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(String id,CategoryRequest request);

    CategoryResponse deleteCategory(String id);

    List<CategoryResponse> getCategories();
}