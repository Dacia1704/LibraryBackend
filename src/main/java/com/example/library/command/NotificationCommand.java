package com.example.library.command;

import com.example.library.messaging.NotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class NotificationCommand {

    private final NotificationProducer notificationProducer;

    /**
     * Gửi thông báo đến người dùng có userId chỉ định
     * 
     * Ví dụ: notification send 1 "Sách của bạn đã quá hạn!"
     */
    @ShellMethod(key = "notification send", value = "Gửi thông báo đến người dùng. Usage: notification send <userId> <content>")
    public String send(Long userId, String content) {
        try {
            notificationProducer.send(userId, content);
            return String.format("Đã gửi thông báo đến userId=%d: \"%s\"", userId, content);
        } catch (Exception e) {
            return String.format("Lỗi khi gửi thông báo: %s", e.getMessage());
        }
    }

    /**
     * Gửi thông báo đến nhiều người dùng
     * 
     * Ví dụ: notification broadcast 1,2,3 "Thông báo chung"
     */
    @ShellMethod(key = "notification broadcast", value = "Gửi thông báo đến nhiều người dùng. Usage: notification broadcast <userId1,userId2,...> <content>")
    public String broadcast(String userIds, String content) {
        try {
            String[] ids = userIds.split(",");
            for (String id : ids) {
                Long userId = Long.parseLong(id.trim());
                notificationProducer.send(userId, content);
            }
            return String.format("Đã gửi thông báo đến %d người dùng: \"%s\"", ids.length, content);
        } catch (Exception e) {
            return String.format("Lỗi khi gửi thông báo: %s", e.getMessage());
        }
    }
}
