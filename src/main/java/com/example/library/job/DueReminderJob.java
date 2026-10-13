package com.example.library.job;

import com.example.library.entity.BorrowDetail;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.messaging.NotificationProducer;
import com.example.library.repository.BorrowDetailRepository;
import com.example.library.repository.SettingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DueReminderJob extends AbstractJob {

    private static final String JOB_NAME = "due-reminder";
    private static final String DUE_REMINDER_DAYS_KEY = "DUE_REMINDER_DAYS";

    final Logger log = LoggerFactory.getLogger(DueReminderJob.class);

    final BorrowDetailRepository borrowDetailRepository;
    final SettingRepository settingRepository;
    final NotificationProducer notificationProducer;
    final TransactionTemplate transactionTemplate;

    @Override
    public String name() {
        return JOB_NAME;
    }

    @Scheduled(
            cron = "${app.jobs.due-reminder.cron}",
            zone = "${app.jobs.due-reminder.zone}"
    )
    public void runScheduled() {
        execute();
    }

    @Override
    protected void doExecute() {
        // Lấy giá trị due-reminder-days từ settings
        int dueReminderDays = getDueReminderDays();
        log.info("Job [{}] configured reminder days: {}", name(), dueReminderDays);

        LocalDate today = LocalDate.now();

        // Lấy tất cả borrow detail đang ở trạng thái BORROWING và OVERDUE
        List<BorrowDetail> borrowingDetails = borrowDetailRepository
                .findAllByStatusWithDetails(BorrowStatus.BORROWING);
        List<BorrowDetail> overdueDetails = borrowDetailRepository
                .findAllByStatusWithDetails(BorrowStatus.OVERDUE);

        log.info("Job [{}] found {} BORROWING, {} OVERDUE borrow details",
                name(), borrowingDetails.size(), overdueDetails.size());

        int remindedCount = 0;

        // Nhóm theo user để gửi thông báo 1 lần cho mỗi user
        Map<Long, StringBuilder> userNotifications = new HashMap<>();

        // Xử lý sách đang mượn (sắp đến hạn)
        for (BorrowDetail detail : borrowingDetails) {
            processDetail(detail, today, dueReminderDays, userNotifications);
        }

        // Xử lý sách quá hạn
        for (BorrowDetail detail : overdueDetails) {
            processOverdueDetail(detail, today, userNotifications);
        }

        // Gửi thông báo cho từng user
        for (Map.Entry<Long, StringBuilder> entry : userNotifications.entrySet()) {
            Long userId = entry.getKey();
            StringBuilder message = entry.getValue();

            try {
                notificationProducer.send(
                        userId,
                        String.format(
                                "Nhắc nhở sách:\n%s\nVui lòng trả sách đúng hạn để tránh bị phạt!",
                                message.toString().trim()
                        )
                );
                remindedCount++;
                log.info("Job [{}] sent reminder to userId={}", name(), userId);
            } catch (Exception e) {
                log.error(
                        "Job [{}] failed to send notification to userId={}: {}",
                        name(),
                        userId,
                        e.getMessage(),
                        e
                );
            }
        }

        log.info("Job [{}] reminded {} users", name(), remindedCount);
    }

    private void processDetail(
            BorrowDetail detail,
            LocalDate today,
            int dueReminderDays,
            Map<Long, StringBuilder> userNotifications
    ) {
        try {
            LocalDate dueDate = detail.getBorrowRecord().getDueDate();
            long daysUntilDue = ChronoUnit.DAYS.between(today, dueDate);

            if (daysUntilDue >= 0 && daysUntilDue <= dueReminderDays) {
                Long userId = detail.getBorrowRecord().getMember().getUser().getId();
                String bookTitle = detail.getBook().getTitle();

                userNotifications
                        .computeIfAbsent(userId, k -> new StringBuilder())
                        .append(String.format(
                                "- Sách \"%s\": còn %d ngày (hạn trả: %s)\n",
                                bookTitle, daysUntilDue, dueDate
                        ));
            }
        } catch (Exception e) {
            log.error(
                    "Job [{}] failed to process borrowDetailId={}: {}",
                    name(),
                    detail.getId(),
                    e.getMessage(),
                    e
            );
        }
    }

    private void processOverdueDetail(
            BorrowDetail detail,
            LocalDate today,
            Map<Long, StringBuilder> userNotifications
    ) {
        try {
            LocalDate dueDate = detail.getBorrowRecord().getDueDate();
            long daysOverdue = ChronoUnit.DAYS.between(dueDate, today);
            Long userId = detail.getBorrowRecord().getMember().getUser().getId();
            String bookTitle = detail.getBook().getTitle();

            userNotifications
                    .computeIfAbsent(userId, k -> new StringBuilder())
                    .append(String.format(
                            "- Sách \"%s\": QUÁ HẠN %d ngày (hạn trả: %s)\n",
                            bookTitle, daysOverdue, dueDate
                    ));
        } catch (Exception e) {
            log.error(
                    "Job [{}] failed to process overdue borrowDetailId={}: {}",
                    name(),
                    detail.getId(),
                    e.getMessage(),
                    e
            );
        }
    }

    private int getDueReminderDays() {
        return settingRepository.findBySettingKeyAndIsDeletedFalse(DUE_REMINDER_DAYS_KEY)
                .map(setting -> {
                    try {
                        return Integer.parseInt(setting.getSettingValue());
                    } catch (NumberFormatException e) {
                        log.warn(
                                "Job [{}] invalid setting value for {}: {}, using default 3",
                                name(),
                                DUE_REMINDER_DAYS_KEY,
                                setting.getSettingValue()
                        );
                        return 3;
                    }
                })
                .orElseGet(() -> {
                    log.warn(
                            "Job [{}] missing setting {}, using default 3",
                            name(),
                            DUE_REMINDER_DAYS_KEY
                    );
                    return 3;
                });
    }
}
