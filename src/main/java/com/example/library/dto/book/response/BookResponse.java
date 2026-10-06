package com.example.library.dto.book.response;

import com.example.library.dto.book_info.response.AuthorResponse;
import com.example.library.dto.book_info.response.CategoryResponse;
import com.example.library.dto.book_info.response.PublisherResponse;
import com.example.library.dto.book_info.response.ShelfResponse;
import com.example.library.entity.enums.Language;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResponse {
    Long id;
    String bookCode;
    String title;
    String isbn;
    Integer publishYear;
    BigDecimal price;
    Integer quantity;
    Integer available;
    BigDecimal width;
    BigDecimal height;
    Integer pages;
    String synopsis;
    Language language;
    ShelfResponse shelf;
    String cover;
    Set<CategoryResponse> categories;
    Set<AuthorResponse> authors;
    Set<PublisherResponse> publishers;
}
