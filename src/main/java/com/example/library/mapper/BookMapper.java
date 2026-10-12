package com.example.library.mapper;

import com.example.library.dto.book.request.BookRequest;
import com.example.library.dto.book.response.BookResponse;
import com.example.library.entity.Book;
import org.mapstruct.*;
import com.example.library.entity.enums.Language;

@Mapper(componentModel = "spring",
        uses = {
                CategoryMapper.class,
                AuthorMapper.class,
                PublisherMapper.class,
                ShelfMapper.class
        })
public interface BookMapper {

    @Mapping(target = "shelf", source = "shelf")
    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "authors", source = "authors")
    @Mapping(target = "publishers", source = "publishers")
    BookResponse toBookResponse(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookCode", ignore = true)
    @Mapping(target = "noAccent", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "shelf", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "publishers", ignore = true)
    @Mapping(target = "borrowDetails", ignore = true)
    @Mapping(target = "cover", ignore = true)
    @Mapping(
            target = "language",
            source = "language",
            defaultExpression = "java(com.example.library.entity.enums.Language.VI)"
    )
    @Mapping(target = "synopsis", source = "synopsis")
    Book toBook(BookRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookCode", ignore = true)
    @Mapping(target = "noAccent", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "shelf", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "publishers", ignore = true)
    @Mapping(target = "borrowDetails", ignore = true)
    @Mapping(target = "cover", ignore = true)
    @Mapping(
            target = "language",
            source = "language",
            defaultExpression = "java(com.example.library.entity.enums.Language.VI)"
    )
    @Mapping(target = "synopsis", source = "synopsis")
    void updateBook(@MappingTarget Book book, BookRequest request);
}
