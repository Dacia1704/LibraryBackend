package com.example.library.dto.authentication.response;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    Long id;
    String email;
    String username;
    String avatar;
    List<String> authorities;
    String accessToken;
    String refreshToken;
}
