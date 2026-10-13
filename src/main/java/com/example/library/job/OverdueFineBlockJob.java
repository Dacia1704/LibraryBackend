package com.example.library.job;

import com.example.library.entity.BorrowDetail;
import com.example.library.entity.Member;
import com.example.library.entity.Setting;
import com.example.library.entity.User;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.messaging.NotificationProducer;
import com.example.library.repository.BorrowDetailRepository;
import com.example.library.repository.FinePaymentRepository;
import com.example.library.repository.FineRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.SettingRepository;
import com.example.library.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OverdueFineBlockJob extends AbstractJob {

    private static final String JOB_NAME = "overdue-fine-block";
    private static final List<BorrowStatus> OVERDUE_CANDIDATE_STATUSES =
            List.of(BorrowStatus.BORROWING, BorrowStatus.OVERDUE);

    final Logger log = LoggerFactory.getLogger(OverdueFineBlockJob.class);

    final MemberRepository memberRepository;
    final BorrowDetailRepository borrowDetailRepository;
    final FineRepository fineRepository;
    final FinePaymentRepository finePaymentRepository;
    final SettingRepository settingRepository;
    final UserRepository userRepository;
    final NotificationProducer notificationProducer;
    final TransactionTemplate transactionTemplate;

    @Value("${app.setting-key.fine-overdue-per-day}")
    String fineOverduePerDayKey;

    @Value("${app.setting-key.max-fine-before-block}")
    String maxFineBeforeBlockKey;

    @Override
    public String name() {
        return JOB_NAME;
    }

    @Scheduled(
            cron = "${app.jobs.overdue-fine-block.cron}",
            zone = "${app.jobs.overdue-fine-block.zone}"
    )
    public void runScheduled() {
        execute();
    }

    @Override
    protected void doExecute() {
        Map<String, BigDecimal> settings = loadSettings();
        BigDecimal finePerDay = settings.get(fineOverduePerDayKey);
        BigDecimal maxFineBeforeBlock = settings.get(maxFineBeforeBlockKey);

        if (finePerDay == null || maxFineBeforeBlock == null) {
            throw new AppException(ErrorCode.SETTING_NOT_FOUND);
        }

        LocalDate today = LocalDate.now();
        List<Member> members = memberRepository.findAllByIsDeletedFalse();
        log.info("Job [{}] processing {} members", name(), members.size());

        for (Member member : members) {
            try {
                transactionTemplate.executeWithoutResult(status -> processMember(member.getId(), today, finePerDay, maxFineBeforeBlock));
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
    }

    private void processMember(
            Long memberId,
            LocalDate today,
            BigDecimal finePerDay,
            BigDecimal maxFineBeforeBlock
    ) {
        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElse(null);
        if (member == null) return;

        List<BorrowDetail> candidates = borrowDetailRepository.findOverdueCandidatesByMember(
                memberId,
                today,
                OVERDUE_CANDIDATE_STATUSES
        );

        for (BorrowDetail detail : candidates) {
            LocalDate dueDate = detail.getBorrowRecord().getDueDate();
            long lateDays = ChronoUnit.DAYS.between(dueDate, today);
            if (lateDays <= 0) {
                continue;
            }
            detail.setStatus(BorrowStatus.OVERDUE);
            detail.setFineAmount(finePerDay.multiply(BigDecimal.valueOf(lateDays)));
        }

        if (!candidates.isEmpty()) {
            borrowDetailRepository.saveAll(candidates);
        }

        BigDecimal overdueTemp = borrowDetailRepository.sumOverdueFineAmountByMember(
                memberId,
                BorrowStatus.OVERDUE
        );
        if (overdueTemp == null) {
            overdueTemp = BigDecimal.ZERO;
        }

        BigDecimal recordedFines = fineRepository.getTotalFineByMember(memberId);
        if (recordedFines == null) {
            recordedFines = BigDecimal.ZERO;
        }

        BigDecimal payments = finePaymentRepository.getTotalPaymentByMember(memberId);
        if (payments == null) {
            payments = BigDecimal.ZERO;
        }

        BigDecimal debt = overdueTemp.add(recordedFines).subtract(payments);

        if (debt.compareTo(maxFineBeforeBlock) <= 0) {
            return;
        }

        User user = member.getUser();
        if (user == null || Boolean.FALSE.equals(user.getIsActive())) {
            return;
        }

        user.setIsActive(false);
        userRepository.save(user);

        notificationProducer.send(
                user.getId(),
                String.format(
                        "Tài khoản tạm thời bị khóa. Phạt đã ghi nhận: %,.0f VNĐ. "
                                + "Phạt tạm tính do trả quá hạn (OVERDUE): %,.0f VNĐ. "
                                + "Tổng nợ: %,.0f VNĐ. "
                                + "Vui lòng trả sách và nộp phạt để mở khóa lại tài khoản.",
                        recordedFines,
                        overdueTemp,
                        debt
                )
        );
    }

    private Map<String, BigDecimal> loadSettings() {
        List<String> keys = List.of(fineOverduePerDayKey, maxFineBeforeBlockKey);
        return settingRepository
                .findAllBySettingKeyInAndIsDeletedFalse(keys)
                .stream()
                .collect(Collectors.toMap(
                        Setting::getSettingKey,
                        setting -> new BigDecimal(setting.getSettingValue())
                ));
    }
}
