package com.example.library.service.impl;

import com.example.library.dto.user.request.MemberRequest;
import com.example.library.dto.user.response.MemberResponse;
import com.example.library.entity.Member;
import com.example.library.entity.User;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.MemberMapper;
import com.example.library.repository.MemberRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.MemberService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberServiceImpl implements MemberService {

    MemberRepository memberRepository;

    UserRepository userRepository;

    MemberMapper memberMapper;

    @Override
    @Transactional
    public MemberResponse createMember(
            MemberRequest request
    ) {

        // Check member code
        if (memberRepository
                .findByMemberCode(request.getMemberCode())
                .isPresent()) {

            throw new AppException(
                    ErrorCode.MEMBER_EXISTED,
                    String.format(
                            "Mã thành viên %s đã tồn tại",
                            request.getMemberCode()
                    )
            );
        }

        // Kiểm tra User
        User user = userRepository
                .findByIdAndIsDeletedFalse(
                        request.getUserId()
                )
                .orElseThrow(() ->
                        new AppException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        // Một User chỉ có một Member
        if (memberRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new AppException(ErrorCode.MEMBER_EXISTED);
        }

        Member member = memberMapper.toMember(request);

        member.setUser(user);
        member.setIsDeleted(false);

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
                .orElseThrow(() ->
                        new AppException(ErrorCode.MEMBER_NOT_FOUND)
                );

        return memberMapper.toMemberResponse(member);
    }
}