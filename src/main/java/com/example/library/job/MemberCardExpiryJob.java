package com.example.library.job;

import com.example.library.entity.Member;
import com.example.library.entity.Role;
import com.example.library.entity.User;
import com.example.library.entity.enums.CardStatus;
import com.example.library.messaging.NotificationProducer;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.RoleRepository;
import com.example.library.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberCardExpiryJob extends AbstractJob {

    private static final String JOB_NAME = "member-card-expiry";
    private static final String READER_ROLE_NAME = "READER";

    final Logger log = LoggerFactory.getLogger(MemberCardExpiryJob.class);

    final MemberRepository memberRepository;
    final RoleRepository roleRepository;
    final UserRepository userRepository;
    final NotificationProducer notificationProducer;
    final TransactionTemplate transactionTemplate;

    @Override
    public String name() {
        return JOB_NAME;
    }

    @Scheduled(
            cron = "${app.jobs.member-card-expiry.cron}",
            zone = "${app.jobs.member-card-expiry.zone}"
    )
    public void runScheduled() {
        execute();
    }

    @Override
    protected void doExecute() {
        LocalDate today = LocalDate.now();
        List<Member> members = memberRepository.findAllByIsDeletedFalse();
        log.info("Job [{}] processing {} members", name(), members.size());

        Role readerRole = roleRepository.findByNameAndIsDeletedFalse(READER_ROLE_NAME)
                .orElse(null);
        if (readerRole == null) {
            log.warn("Job [{}] could not find READER role", name());
            return;
        }

        int expiredCount = 0;
        for (Member member : members) {
            try {
                boolean expired = transactionTemplate.execute(status ->
                        processMember(member.getId(), today, readerRole)
                );
                if (expired) {
                    expiredCount++;
                }
            } catch (Exception e) {
                log.error(
                        "Job [{}] failed for memberId={}: {}",
                        name(),
                        member.getId(),
                        e.getMessage(),
                        e
                );
            }
        }
        log.info("Job [{}] expired {} member cards", name(), expiredCount);
    }

    private boolean processMember(
            Long memberId,
            LocalDate today,
            Role readerRole
    ) {
        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElse(null);
        if (member == null) return false;

        if (!member.getCardExpiry().isBefore(today)) {
            return false;
        }

        User user = member.getUser();
        if (user == null) return false;

        Role currentRole = user.getRole();
        // Nếu đã là READER rồi thì không cần xử lý
        if (currentRole != null && READER_ROLE_NAME.equals(currentRole.getName())) {
            return false;
        }

        // Chuyển role về READER
        user.setRole(readerRole);
        userRepository.save(user);

        // Gửi thông báo cho member
        notificationProducer.send(
                user.getId(),
                String.format(
                        "Thẻ thư viện của bạn đã hết hạn vào ngày %s. "
                                + "Tài khoản của bạn đã được chuyển về quyền ĐỘC GIẢ. "
                                + "Vui lòng gia hạn thẻ để tiếp tục mượn sách.",
                        member.getCardExpiry()
                )
        );

        log.info("Job [{}] expired member {} card, role changed to READER", name(), memberId);
        return true;
    }
}
