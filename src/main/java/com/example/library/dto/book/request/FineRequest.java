package com.example.library.dto.book.request;

import com.example.library.entity.enums.FineReason;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineRequest {
    @NotNull
    FineReason reason;

    String note;

    MultipartFile attachment;
}
