package com.example.library.dto.book.response;

import com.example.library.dto.category_author_publisher.response.AuthorResponse;
import com.example.library.dto.category_author_publisher.response.CategoryResponse;
import com.example.library.dto.category_author_publisher.response.PublisherResponse;
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
    String title;
    String isbn;
    Integer publishYear;
    Integer quantity;
    Integer available;
    String cover;
    BigDecimal price;
    Set<CategoryResponse> categories;
    Set<AuthorResponse> authors;
    Set<PublisherResponse> publishers;
}
