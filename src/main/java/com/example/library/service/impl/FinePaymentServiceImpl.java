package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.book.request.FinePaymentRequest;
import com.example.library.dto.book.response.FinePaymentResponse;
import com.example.library.entity.FinePayment;
import com.example.library.entity.Member;
import com.example.library.entity.Setting;
import com.example.library.entity.User;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.mapper.FinePaymentMapper;
import com.example.library.messaging.NotificationProducer;
import com.example.library.repository.BorrowDetailRepository;
import com.example.library.repository.FinePaymentRepository;
import com.example.library.repository.FineRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.SettingRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.FinePaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FinePaymentServiceImpl implements FinePaymentService {

    final FinePaymentRepository finePaymentRepository;
    final MemberRepository memberRepository;
    final UserRepository userRepository;
    final FinePaymentMapper finePaymentMapper;
    final NotificationProducer notificationProducer;
    final FineRepository fineRepository;
    final BorrowDetailRepository borrowDetailRepository;
    final SettingRepository settingRepository;

    @Value("${app.setting-key.max-fine-before-block}")
    String maxFineBeforeBlockKey;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FinePaymentResponse> getAll(
            Long memberId,
            int page,
            int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<FinePayment> paymentPage;

        if (memberId != null) {
            paymentPage = finePaymentRepository
                    .findAllByMemberIdAndIsDeletedFalse(
                            memberId,
                            pageable
                    );
        } else {
            paymentPage = finePaymentRepository
                    .findAllByIsDeletedFalse(pageable);
        }

        List<FinePaymentResponse> content = paymentPage.getContent()
                .stream()
                .map(finePaymentMapper::toResponse)
                .toList();

        return PageResponse.<FinePaymentResponse>builder()
                .data(content)
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FinePaymentResponse> getMe(
            int page,
            int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Long userId = Long.valueOf(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );

        Page<FinePayment> paymentPage =
                finePaymentRepository.findAllByUserId(
                        userId,
                        pageable
                );

        List<FinePaymentResponse> content = paymentPage.getContent()
                .stream()
                .map(finePaymentMapper::toResponse)
                .toList();

        return PageResponse.<FinePaymentResponse>builder()
                .data(content)
                .currentPage(paymentPage.getNumber())
                .pageSize(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional
    public FinePaymentResponse create(FinePaymentRequest request) {

        Long userId = getCurrentUserId();

        Member member = memberRepository
                .findByIdAndIsDeletedFalse(request.getMemberId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Member not found"));

        User receivedBy = userRepository
                .findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        // Validate: chỉ thanh toán được số tiền phạt chính thức còn lại trong bảng Fine
        BigDecimal recordedFines = fineRepository.getTotalFineByMember(member.getId());
        if (recordedFines == null) {
            recordedFines = BigDecimal.ZERO;
        }
        System.out.println(recordedFines);

        BigDecimal payments = finePaymentRepository.getTotalPaymentByMember(member.getId());
        if (payments == null) {
            payments = BigDecimal.ZERO;
        }
        System.out.println(payments);

        BigDecimal remainingOfficialFines = recordedFines.subtract(payments);
        if (remainingOfficialFines.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Không có khoản phạt chính thức nào cần thanh toán.");
        }

        if (request.getAmount().compareTo(remainingOfficialFines) > 0) {
            throw new IllegalArgumentException(
                    String.format(
                            "Số tiền thanh toán (%,.0f VNĐ) vượt quá số tiền phạt chính thức còn lại (%,.0f VNĐ).",
                            request.getAmount(),
                            remainingOfficialFines
                    )
            );
        }

        FinePayment payment = FinePayment.builder()
                .member(member)
                .amount(request.getAmount())
                .receivedBy(receivedBy)
                .note(request.getNote())
                .isDeleted(false)
                .build();

        payment = finePaymentRepository.save(payment);
        recordedFines = fineRepository.getTotalFineByMember(member.getId());
        payments = finePaymentRepository.getTotalPaymentByMember(member.getId());


        BigDecimal overdueTemp = borrowDetailRepository.sumOverdueFineAmountByMember(
                member.getId(),
                BorrowStatus.OVERDUE
        );
        if (overdueTemp == null) {
            overdueTemp = BigDecimal.ZERO;
        }

        BigDecimal totalDebt = overdueTemp.add(recordedFines).subtract(payments);

        BigDecimal maxFineBeforeBlock = settingRepository
                .findBySettingKeyAndIsDeletedFalse(maxFineBeforeBlockKey)
                .map(Setting::getSettingValue)
                .map(BigDecimal::new)
                .orElseThrow(() -> new IllegalStateException("Missing setting: " + maxFineBeforeBlockKey));

        User memberUser = member.getUser();
        boolean unlocked = false;
        if (totalDebt.compareTo(maxFineBeforeBlock) <= 0
                && memberUser != null
                && Boolean.FALSE.equals(memberUser.getIsActive())) {
            memberUser.setIsActive(true);
            userRepository.save(memberUser);
            unlocked = true;
        }



        BigDecimal stillOfficial = remainingOfficialFines.subtract(request.getAmount());
        BigDecimal stillOverdue = overdueTemp;

        if (stillOfficial.compareTo(BigDecimal.ZERO) > 0 || stillOverdue.compareTo(BigDecimal.ZERO) > 0) {
            StringBuilder message = new StringBuilder();
            message.append(String.format(
                    "Bạn đã trả %,.0f VNĐ tiền phạt thành công. ",
                    request.getAmount()
            ));
            boolean first = true;
            if (stillOfficial.compareTo(BigDecimal.ZERO) > 0) {
                message.append(String.format("Phạt ghi nhận còn lại: %,.0f VNĐ.", stillOfficial));
                first = false;
            }
            if (stillOverdue.compareTo(BigDecimal.ZERO) > 0) {
                if (!first) message.append(" ");
                message.append(String.format("Phạt quá hạn (chưa ghi nhận) còn: %,.0f VNĐ.", stillOverdue));
            }
            if (unlocked) {
                message.append(" Tài khoản của bạn đã được mở khóa lại.");
            }
            notificationProducer.send(memberUser.getId(), message.toString());
        } else {
            String message = String.format(
                    "Bạn đã thanh toán toàn bộ tiền phạt (%,.0f VNĐ). Cố giữ vững quy định của thư viện nhé!",
                    request.getAmount()
            );
            if (unlocked) {
                message += " Tài khoản của bạn đã được mở khóa lại.";
            }
            notificationProducer.send(memberUser.getId(), message);
        }

        return finePaymentMapper.toResponse(payment);
    }

    @Override
    public BigDecimal getTotalByUserId(Long userId) {
        return finePaymentRepository.getTotalPaymentByUserId(userId);
    }

    @Override
    public BigDecimal getTotalByMemberId(Long memberId) {
        return finePaymentRepository.getTotalPaymentByMember(memberId);
    }

    @Override
    public BigDecimal getMyTotal() {

        return getTotalByUserId(getCurrentUserId());
    }

    private Long getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return Long.valueOf(authentication.getName());
    }
}