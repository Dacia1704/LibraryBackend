package com.example.library.mapper;

import com.example.library.dto.book_info.request.AuthorRequest;
import com.example.library.dto.book_info.response.AuthorResponse;
import com.example.library.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorResponse toAuthorResponse(Author author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "noAccent", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "books", ignore = true)
    Author toAuthor(AuthorRequest request);
}