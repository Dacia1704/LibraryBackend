
package com.example.library.service;


import com.example.library.common.PageResponse;
import com.example.library.dto.user.request.CreateUserMemberRequest;
import com.example.library.dto.user.request.MemberCreateRequest;
import com.example.library.dto.user.request.MemberFilter;
import com.example.library.dto.user.request.MemberRenewRequest;
import com.example.library.dto.user.request.MemberRequest;
import com.example.library.dto.user.response.MemberResponse;

import java.math.BigDecimal;
import java.util.List;

public interface MemberService {

    MemberResponse createMember(MemberCreateRequest request);
    MemberResponse createUserAndMember(CreateUserMemberRequest request);
    MemberResponse renewMember(Long id,MemberRenewRequest request);

    MemberResponse updateMember(Long id, MemberRequest request);

    MemberResponse deleteMember(String id);

    MemberResponse getMember(String id);
    MemberResponse getMe();

    List<MemberResponse> getMembers(String keyword);

    PageResponse<MemberResponse> getMembersPagination(
            MemberFilter filter,
            int page,
            int size
    );

    MemberResponse getMemberDeleted(String id);
}