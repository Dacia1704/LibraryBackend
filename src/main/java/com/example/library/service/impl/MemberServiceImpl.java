package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.user.request.CreateUserMemberRequest;
import com.example.library.dto.user.request.MemberCreateRequest;
import com.example.library.dto.user.request.MemberFilter;
import com.example.library.dto.user.request.MemberRenewRequest;
import com.example.library.dto.user.request.MemberRequest;
import com.example.library.dto.user.response.MemberResponse;
import com.example.library.entity.Member;
import com.example.library.entity.MemberPayment;
import com.example.library.entity.Role;
import com.example.library.entity.Setting;
import com.example.library.entity.User;
import com.example.library.entity.enums.CardStatus;
import com.example.library.entity.enums.PaymentType;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.MemberMapper;
import com.example.library.mapper.UserMapper;
import com.example.library.repository.MemberPaymentRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.RoleRepository;
import com.example.library.repository.SettingRepository;
import com.example.library.repository.UserRepository;
import com.example.library.repository.specification.MemberSpecification;
import com.example.library.service.MemberService;
import com.example.library.utils.TextUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MemberServiceImpl implements MemberService {

    final MemberRepository memberRepository;

    final UserRepository userRepository;

    final RoleRepository roleRepository;

    final SettingRepository settingRepository;

    final MemberMapper memberMapper;

    final UserMapper userMapper;

    final MemberPaymentRepository memberPaymentRepository;

    final PasswordEncoder passwordEncoder;

    @Value("${app.setting-key.member-payment-month}")
    String memberPaymentMonthKey;

    @Value("${app.setting-key.card-maker-fee}")
    String cardMakerFeeKey;

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
    public MemberResponse createUserAndMember(
            CreateUserMemberRequest request
    ) {

        // Check username
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(
                    ErrorCode.USER_EXISTED,
                    String.format(
                            "Username %s đã tồn tại",
                            request.getUsername()
                    )
            );
        }

        // Check email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(
                    ErrorCode.EMAIL_EXISTED,
                    String.format(
                            "Email %s đã tồn tại",
                            request.getEmail()
                    )
            );
        }

        // Check phone đã tồn tại chưa
        if (request.getPhone() != null
                && !request.getPhone().isBlank()
                && memberRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new AppException(
                    ErrorCode.MEMBER_EXISTED,
                    String.format(
                            "Thành viên với số điện thoại %s đã tồn tại",
                            request.getPhone()
                    )
            );
        }

        // Tìm role mặc định là READER
        Role role = roleRepository
                .findByNameAndIsDeletedFalse("READER")
                .orElseThrow(() ->
                        new AppException(ErrorCode.ROLE_NOT_FOUND)
                );

        // ===== 1. Tạo User =====
        User user = userMapper.toUser(request);
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );
        user.setNoAccent(
                TextUtils.removeAccent(request.getFullName())
        );
        user.setRole(role);
        user.setIsActive(true);
        user.setIsDeleted(false);
        user.setFailedAttempts(0);

        userRepository.save(user);

        // ===== 2. Tạo Member =====
        Member member = new Member();
        member.setUser(user);
        member.setIdentityNumber(request.getIdentityNumber());
        member.setPhone(request.getPhone());
        member.setAddress(request.getAddress());
        member.setIsDeleted(false);
        member.setMemberCode(generateMemberCode());
        member.setCardExpiry(LocalDate.now().plusMonths(request.getMonthRequest()));
        member.setCardStatus(CardStatus.PENDING);

        // ===== 3. Ghi MemberPayment =====
        int month = request.getMonthRequest();

        List<String> settingKeys = List.of(
                memberPaymentMonthKey,
                cardMakerFeeKey
        );

        Map<String, BigDecimal> paymentSetting = settingRepository
                .findAllBySettingKeyInAndIsDeletedFalse(settingKeys)
                .stream()
                .collect(Collectors.toMap(
                        Setting::getSettingKey,
                        setting -> new BigDecimal(setting.getSettingValue())
                ));

        BigDecimal memberPaymentMonth = paymentSetting.get(memberPaymentMonthKey);
        BigDecimal cardMakerFee = paymentSetting.get(cardMakerFeeKey);

        if (memberPaymentMonth == null || cardMakerFee == null) {
            throw new AppException(
                    ErrorCode.SETTING_NOT_FOUND,
                    "Không tìm thấy cấu hình phí thành viên hoặc phí làm thẻ"
            );
        }

        BigDecimal requiredAmount = memberPaymentMonth
                .multiply(BigDecimal.valueOf(month))
                .add(cardMakerFee);

        if (request.getAmount() == null
                || request.getAmount().compareTo(requiredAmount) < 0) {

            throw new AppException(
                    ErrorCode.PAYMENT_AMOUNT_NOT_ENOUGH,
                    String.format(
                            "Số tiền thanh toán không đủ. Cần tối thiểu %s (tháng đăng ký %s * %s + phí làm thẻ %s)",
                            requiredAmount,
                            month,
                            memberPaymentMonth,
                            cardMakerFee
                    )
            );
        }

        Long userId = Long.valueOf(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName()
        );
        User receiver = userRepository
                .findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        MemberPayment memberPayment = MemberPayment.builder()
                .member(member)
                .amount(request.getAmount())
                .paymentType(PaymentType.REGISTER)
                .receivedBy(receiver)
                .note(
                        String.format(
                                "Đăng kí thành viên %d tháng",
                                month
                        )
                )
                .build();
        memberRepository.save(member);
        memberPaymentRepository.save(memberPayment);


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
    public MemberResponse getMe() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        Member member = memberRepository
                .findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));
        return memberMapper.toMemberResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> getMembers(String keyword) {
        List<Member> members;
        if (keyword != null && !keyword.isBlank()) {
            String normalizedKeyword = TextUtils.removeAccent(keyword.toLowerCase().trim());
            members = memberRepository.findAllByIsDeletedFalse().stream()
                    .filter(member -> {
                        String fullName = member.getUser() != null && member.getUser().getNoAccent() != null 
                                ? member.getUser().getNoAccent().toLowerCase() : "";
                        String phone = member.getPhone() != null ? member.getPhone().toLowerCase() : "";
                        String memberCode = member.getMemberCode() != null ? member.getMemberCode().toLowerCase() : "";
                        return fullName.contains(normalizedKeyword) || phone.contains(normalizedKeyword) || memberCode.contains(normalizedKeyword);
                    })
                    .toList();
        } else {
            members = memberRepository.findAllByIsDeletedFalse();
        }
        return members.stream()
                .map(memberMapper::toMemberResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MemberResponse> getMembersPagination(
            MemberFilter filter,
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

        Specification<Member> specification =
                MemberSpecification.filter(filter);

        Page<Member> memberPage =
                memberRepository.findAll(specification, pageable);

        List<MemberResponse> content = memberPage.getContent()
                .stream()
                .map(memberMapper::toMemberResponse)
                .toList();

        return PageResponse.<MemberResponse>builder()
                .data(content)
                .currentPage(memberPage.getNumber())
                .pageSize(memberPage.getSize())
                .totalElements(memberPage.getTotalElements())
                .totalPages(memberPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMemberDeleted(String id) {

        Member member = memberRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() -> new AppException(ErrorCode.MEMBER_NOT_FOUND));

        return memberMapper.toMemberResponse(member);
    }

    public String generateMemberCode() {
        return TextUtils.generateCode("MB");
    }
}