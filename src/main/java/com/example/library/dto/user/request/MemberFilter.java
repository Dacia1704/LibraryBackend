package com.example.library.dto.user.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberFilter {
    String keyword;
    String role;          // admin/librarian/reader/member
    Boolean hasCard;      // true/false
    String email;         // Lọc theo email
    String phone;         // Lọc theo SĐT
    Boolean showDeleted;  // Hiển thị đã xóa
}