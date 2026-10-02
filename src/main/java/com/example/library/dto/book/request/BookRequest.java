package com.example.library.dto.book.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

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

    @NotNull
    Integer quantity;

    @NotNull
    Integer available;

    MultipartFile cover;

    Set<Long> categoryIds;

    Set<Long> authorIds;

    Set<Long> publisherIds;

}
