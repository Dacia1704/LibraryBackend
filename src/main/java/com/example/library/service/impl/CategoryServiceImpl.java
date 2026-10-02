package com.example.library.service.impl;

import com.example.library.dto.category_author_publisher.request.CategoryRequest;
import com.example.library.dto.category_author_publisher.response.CategoryResponse;
import com.example.library.entity.Category;
import com.example.library.mapper.CategoryMapper;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.CategoryService;
import com.example.library.utils.TextUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryRequest request) {

        Category category = categoryMapper.toCategory(request);

        category.setNoAccent(TextUtils.removeAccent(request.getName()));
        category.setIsDeleted(false);

        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategory(String id, CategoryRequest request) {

        Category category = categoryRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() ->
                        new RuntimeException("Category không tồn tại")
                );

        category.setName(request.getName());
        category.setNoAccent(TextUtils.removeAccent(request.getName()));

        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse deleteCategory(String id) {

        Category category = categoryRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() ->
                        new RuntimeException("Category không tồn tại")
                );

        category.setIsDeleted(true);

        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> getCategories() {

        return categoryRepository.findAllByIsDeletedFalse()
                .stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }
}