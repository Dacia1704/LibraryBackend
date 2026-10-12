package com.example.library.messaging;

import com.example.library.config.RabbitConfig;
import com.example.library.dto.notification.response.NotificationResponse;
import com.example.library.entity.Notification;
import com.example.library.entity.User;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.repository.NotificationRepository;
import com.example.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationRepository notificationRepo;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handle(NotificationMessage msg) {
        Notification n = new Notification();
        User user = userRepository.findById(msg.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        n.setUser(user);
        n.setContent(msg.getContent());
        n = notificationRepo.save(n);

        // Gửi WebSocket đến user_id
        messagingTemplate.convertAndSendToUser(
            user.getId().toString(), "/queue/notifications",
            NotificationResponse.builder()
                    .id(n.getId())
                    .userId(user.getId())
                    .content(n.getContent())
                    .read(n.getIsRead())
                    .createdAt(n.getCreatedAt())
                    .build());
    }
}