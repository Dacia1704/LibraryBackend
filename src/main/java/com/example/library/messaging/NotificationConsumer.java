package com.example.library.messaging;

import com.example.library.config.RabbitConfig;
import com.example.library.dto.notification.response.NotificationResponse;
import com.example.library.entity.Member;
import com.example.library.entity.Notification;
import com.example.library.entity.User;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationRepository notificationRepo;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handle(NotificationMessage msg) {
        Notification n = new Notification();
        Member member = memberRepository.findById(msg.getMemberId()).orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        n.setMember(member);
        n.setContent(msg.getContent());
        n = notificationRepo.save(n);

        User user = member.getUser();

        // Lấy user_id của member để đẩy đúng người
        messagingTemplate.convertAndSendToUser(
            user.getId().toString(), "/queue/notifications",
            NotificationResponse.builder()
                    .id(n.getId())
                    .memberId(member.getId())
                    .content(n.getContent())
                    .read(n.getIsRead())
                    .createdAt(n.getCreatedAt())
                    .build());
    }
}