package com.example.library.dto.book.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingResponse {

    private Long id;

    private String settingKey;

    private String settingValue;

    private String description;

    private LocalDateTime updatedAt;

    private Boolean isDeleted;
}