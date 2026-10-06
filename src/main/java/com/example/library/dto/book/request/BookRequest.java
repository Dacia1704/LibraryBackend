package com.example.library.dto.book.request;

import com.example.library.entity.enums.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookRequest {

    @NotBlank
    String title;

    @NotBlank
    String isbn;

    Integer publishYear;

    BigDecimal price;

    @NotNull
    Integer quantity;

    @NotNull
    Integer available;

    BigDecimal width;

    BigDecimal height;

    Integer pages;

    String sysnopsis;

    Language language;

    Long shelfId;

    MultipartFile cover;

    Set<Long> categoryIds;

    Set<Long> authorIds;

    Set<Long> publisherIds;

}
