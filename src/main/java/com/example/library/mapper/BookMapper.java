package com.example.library.mapper;

import com.example.library.dto.book.request.BookRequest;
import com.example.library.dto.book.response.BookResponse;
import com.example.library.entity.Book;
import org.mapstruct.*;

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
    Book toBook(BookRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cover", ignore = true)
    void updateBook(@MappingTarget Book book, BookRequest request);
}