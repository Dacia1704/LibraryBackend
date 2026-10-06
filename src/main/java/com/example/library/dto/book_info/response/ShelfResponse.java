package com.example.library.dto.book_info.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShelfResponse {
    Long id;
    String code;
    String name;
    String location;
}
