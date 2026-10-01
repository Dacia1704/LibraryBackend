package com.example.library.mapper;

import com.example.library.dto.category_author_publisher.request.CategoryRequest;
import com.example.library.dto.category_author_publisher.response.CategoryResponse;
import com.example.library.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "noAccent", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "books", ignore = true)
    Category toCategory(CategoryRequest request);
}