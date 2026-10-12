package com.example.library.service.impl;

import com.example.library.dto.notification.response.NotificationResponse;
import com.example.library.entity.Notification;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.NotificationMapper;
import com.example.library.repository.NotificationRepository;
import com.example.library.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationServiceImpl implements NotificationService {

    final NotificationRepository notificationRepository;
    final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(Boolean isRead) {
        Long userId = getCurrentUserId();

        List<Notification> notifications;

        if (isRead != null) {
            notifications = notificationRepository.findByUserIdAndIsReadAndIsDeletedFalse(userId, isRead);
        } else {
            notifications = notificationRepository.findByUserIdAndIsDeletedFalse(userId);
        }

        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse markAsReadMe(Long notificationId) {
        Long userId = getCurrentUserId();

        Notification notification = notificationRepository.findByIdAndIsDeletedFalse(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        // Kiểm tra notification thuộc về user hiện tại
        if (!notification.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }

        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        return notificationMapper.toNotificationResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsReadMe() {
        Long userId = getCurrentUserId();

        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadAndIsDeletedFalse(userId, false);

        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void deleteMe(Long notificationId) {
        Long userId = getCurrentUserId();

        Notification notification = notificationRepository.findByIdAndIsDeletedFalse(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        // Kiểm tra notification thuộc về user hiện tại
        if (!notification.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }

        notification.setIsDeleted(true);
        notificationRepository.save(notification);
    }

    private Long getCurrentUserId() {
        return Long.valueOf(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
    }
}
