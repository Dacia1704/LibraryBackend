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

    BookResponse toBookResponse(Book book);

    @Mapping(target = "cover", ignore = true)
    @Mapping(
            target = "language",
            source = "language",
            defaultExpression = "java(com.example.library.entity.enums.Language.VI)"
    )
    Book toBook(BookRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(
            target = "language",
            source = "language",
            defaultExpression = "java(com.example.library.entity.enums.Language.VI)"
    )
    @Mapping(target = "cover", ignore = true)
    void updateBook(@MappingTarget Book book, BookRequest request);
}