package com.example.library.dto.category_author_publisher.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorRequest {

    @NotNull
    @NotBlank
    String name;

    @NotBlank
    String bio;

}
