package com.example.library.dto.notification.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationFilter {
    Long userId;         // Lọc theo user
    Boolean isRead;      // Lọc theo trạng thái đã đọc/chưa đọc
    String keyword;      // Tìm kiếm trong content
}
