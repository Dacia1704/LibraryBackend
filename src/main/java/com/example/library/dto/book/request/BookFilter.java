package com.example.library.dto.book.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookFilter {
    String keyword;
    String isbn;
    Integer maxPublishYear;
    Integer minPublishYear;
    Integer minQuantity;
    Integer maxQuantity;
    Integer minAvailable;
    Integer maxAvailable;
    Set<Long> categoryIds;
    Set<Long> authorIds;
    Set<Long> publisherIds;
}
