package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.notification.response.NotificationResponse;
import com.example.library.messaging.NotificationProducer;
import com.example.library.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    NotificationService notificationService;
    NotificationProducer notificationProducer;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<NotificationResponse>> getMyNotifications(
            @RequestParam(required = false) Boolean isRead
    ) {
        return ApiResponse.success(notificationService.getMyNotifications(isRead));
    }

    @PutMapping("/me/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<NotificationResponse> markAsRead(
            @PathVariable Long id
    ) {
        return ApiResponse.success(notificationService.markAsReadMe(id));
    }

    @PutMapping("/me/read-all")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsReadMe();
        return ApiResponse.<Void>builder().code(200).message("Đánh dấu tất cả đã đọc").build();
    }

    @DeleteMapping("/me/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> delete(
            @PathVariable Long id
    ) {
        notificationService.deleteMe(id);
        return ApiResponse.<Void>builder().code(200).message("Xóa thông báo thành công").build();
    }

    // ===== ADMIN ENDPOINTS =====

    /**
     * Gửi thông báo đến người dùng cụ thể
     * Chỉ admin mới có quyền
     */
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> sendNotification(
            @RequestParam Long userId,
            @RequestParam String content
    ) {
        notificationProducer.send(userId, content);
        return ApiResponse.<Void>builder()
                .code(200)
                .message(String.format("Đã gửi thông báo đến userId=%d", userId))
                .build();
    }

    /**
     * Gửi thông báo đến nhiều người dùng
     * Chỉ admin mới có quyền
     */
    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> broadcastNotification(
            @RequestParam String userIds,  // format: "1,2,3"
            @RequestParam String content
    ) {
        String[] ids = userIds.split(",");
        for (String id : ids) {
            try {
                Long userId = Long.parseLong(id.trim());
                notificationProducer.send(userId, content);
            } catch (NumberFormatException ignored) {}
        }
        return ApiResponse.<Void>builder()
                .code(200)
                .message(String.format("Đã gửi thông báo đến %d người dùng", ids.length))
                .build();
    }
}
