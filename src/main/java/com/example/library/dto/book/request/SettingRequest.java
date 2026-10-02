package com.example.library.dto.book.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingRequest {

    @NotBlank
    @Size(max = 100)
    private String settingKey;

    @NotBlank
    @Size(max = 500)
    private String settingValue;

    @Size(max = 255)
    private String description;
}