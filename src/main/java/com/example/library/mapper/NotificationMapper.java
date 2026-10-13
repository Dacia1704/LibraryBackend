package com.example.library.mapper;

import com.example.library.dto.notification.request.NotificationRequest;
import com.example.library.dto.notification.response.NotificationResponse;
import com.example.library.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Notification toNotification(NotificationRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "read", source = "isRead")
    NotificationResponse toNotificationResponse(Notification notification);

    void updateNotification(
            @MappingTarget Notification notification,
            NotificationRequest request
    );
}
