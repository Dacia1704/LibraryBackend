package com.example.library.service.impl;

import com.example.library.dto.user.request.MemberCreateRequest;
import com.example.library.dto.user.request.MemberRenewRequest;
import com.example.library.dto.user.request.MemberRequest;
import com.example.library.dto.user.response.MemberResponse;
import com.example.library.entity.Member;
import com.example.library.entity.MemberPayment;
import com.example.library.entity.Setting;
import com.example.library.entity.User;
import com.example.library.entity.enums.PaymentType;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.MemberMapper;
import com.example.library.repository.MemberPaymentRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.SettingRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.MemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberServiceImpl implements MemberService {

    final MemberRepository memberRepository;

    final UserRepository userRepository;

    final MemberMapper memberMapper;

    final MemberPaymentRepository memberPaymentRepository;

    final SettingRepository settingRepository;

    @Value("${app.setting-key.member-payment-month}")
    String member_payment_month;

    @Override
    @Transactional
    public MemberResponse createMember(
            MemberCreateRequest request
    ) {

        // Check member
        if (memberRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new AppException(ErrorCode.MEMBER_EXISTED, String.format("Thành viên với số điện thoại %s đã tồn tại", request.getPhone()));
        }

        // Kiểm tra User
        User user = userRepository.findByIdAndIsDeletedFalse(request.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Một User chỉ có một Member
        if (memberRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new AppException(ErrorCode.MEMBER_EXISTED);
        }

        Long userId = Long.valueOf(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );
        User receiver = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));


        Member member = memberMapper.toMember(request);

        member.setUser(user);
        member.setIsDeleted(false);
        member.setMemberCode(generateMemberCode());
        member.setCardExpiry(LocalDate.now().plusMonths(1));

        MemberPayment memberPayment = MemberPayment.builder()
                .member(member)
                .amount(request.getAmount())
                .paymentType(PaymentType.REGISTER)
                .receivedBy(receiver)
                .note("Đăng kí thành viên 1 tháng")
                .build();
        memberPaymentRepository.save(memberPayment);


        memberRepository.save(member);

        return memberMapper.toMemberResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse renewMember(Long id, MemberRenewRequest request) {

        Member member = memberRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDate today = LocalDate.now();

        LocalDate startDate = member.getCardExpiry() != null
                && member.getCardExpiry().isAfter(today)
                ? member.getCardExpiry()
                : today;

        member.setCardExpiry(startDate.plusMonths(1));

        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        User receiver = userRepository.findByIdAndIsDeletedFalse(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        MemberPayment memberPayment = MemberPayment.builder()
                .member(member)
                .amount(request.getAmount())
                .paymentType(PaymentType.RENEW)
                .receivedBy(receiver)
                .note("Gia hạn thành viên 1 tháng")
                .build();
        memberPaymentRepository.save(memberPayment);

        memberRepository.save(member);

        return memberMapper.toMemberResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse updateMember(
            Long id,
            MemberRequest request
    ) {

        Member member = memberRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        // Check member code
        Optional<Member> memberExist =
                memberRepository.findByMemberCode(
                        request.getMemberCode()
                );

        if (memberExist.isPresent()
                && !memberExist.get().getId().equals(id)) {

            throw new AppException(
                    ErrorCode.MEMBER_EXISTED,
                    String.format("Mã thành viên %s đã tồn tại", request.getMemberCode()));
        }

        User user = userRepository
                .findByIdAndIsDeletedFalse(
                        request.getUserId()
                )
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        Optional<Member> userMember =
                memberRepository.findByUserId(
                        request.getUserId()
                );

        if (userMember.isPresent()
                && !userMember.get().getId().equals(id)) {

            throw new AppException(ErrorCode.MEMBER_EXISTED);
        }

        memberMapper.updateMember(
                member,
                request
        );

        member.setUser(user);

        memberRepository.save(member);

        return memberMapper.toMemberResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse deleteMember(String id) {

        Member member = memberRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() ->
                        new AppException(ErrorCode.MEMBER_NOT_FOUND)
                );

        member.setIsDeleted(true);

        memberRepository.save(member);

        return memberMapper.toMemberResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMember(String id) {

        Member member = memberRepository
                .findByIdAndIsDeletedFalse(
                        Long.valueOf(id)
                )
                .orElseThrow(() ->
                        new AppException(ErrorCode.MEMBER_NOT_FOUND)
                );

        return memberMapper.toMemberResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers() {

        return memberRepository
                .findAllByIsDeletedFalse()
                .stream()
                .map(memberMapper::toMemberResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberDeleted(String id) {

        Member member = memberRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        return memberMapper.toMemberResponse(member);
    }

    @Override
    public BigDecimal getMemberFeeMonth() {
        Setting setting = settingRepository.findBySettingKeyAndIsDeletedFalse(member_payment_month).orElseThrow(() -> new AppException(ErrorCode.SETTING_NOT_FOUND));
        return new BigDecimal(setting.getSettingValue());
    }

    public String generateMemberCode() {
        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String random = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();

        return "MB-" + date + "-" + random;
    }
}