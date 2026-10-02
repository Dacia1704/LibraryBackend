package com.example.library.dto.user.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilter {
    String keyword;
    String email;
    Long roleId;
    Boolean isMember;
    String phone;
}
