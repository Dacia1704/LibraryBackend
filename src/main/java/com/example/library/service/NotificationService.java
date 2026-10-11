package com.example.library.service;

import com.example.library.dto.notification.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getMyNotifications(Boolean isRead);

    NotificationResponse markAsReadMe(Long notificationId);

    void markAllAsReadMe();

    void deleteMe(Long notificationId);
}
