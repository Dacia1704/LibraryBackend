package com.example.library.service.impl;

import com.example.library.dto.notification.response.NotificationResponse;
import com.example.library.entity.Member;
import com.example.library.entity.Notification;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.NotificationMapper;
import com.example.library.repository.MemberRepository;
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
    final MemberRepository memberRepository;
    final NotificationMapper notificationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(Boolean isRead) {
        Long userId = getCurrentUserId();
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        List<Notification> notifications;

        if (isRead != null) {
            notifications = notificationRepository.findByMemberIdAndIsReadAndIsDeletedFalse(member.getId(), isRead);
        } else {
            notifications = notificationRepository.findByMemberIdAndIsDeletedFalse(member.getId());
        }

        return notifications.stream()
                .map(notificationMapper::toNotificationResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse markAsReadMe(Long notificationId) {
        Long userId = getCurrentUserId();
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = notificationRepository.findByIdAndIsDeletedFalse(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        // Kiểm tra notification thuộc về member hiện tại
        if (!notification.getMember().getId().equals(member.getId())) {
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
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        List<Notification> notifications = notificationRepository
                .findByMemberIdAndIsReadAndIsDeletedFalse(member.getId(), false);

        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void deleteMe(Long notificationId) {
        Long userId = getCurrentUserId();
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = notificationRepository.findByIdAndIsDeletedFalse(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        // Kiểm tra notification thuộc về member hiện tại
        if (!notification.getMember().getId().equals(member.getId())) {
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
