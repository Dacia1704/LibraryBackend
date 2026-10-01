package com.example.library.dto.role_permission.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {

    @NotNull
    @NotBlank
    String name;

    @NotBlank
    String description;

    @NotNull
    List<Long> permissionIds;
}
