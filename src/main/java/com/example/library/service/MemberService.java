package com.example.library.service;


import com.example.library.dto.user.request.MemberRequest;
import com.example.library.dto.user.response.MemberResponse;

import java.util.List;

public interface MemberService {

    MemberResponse createMember(MemberRequest request);

    MemberResponse updateMember(Long id, MemberRequest request);

    MemberResponse deleteMember(String id);

    MemberResponse getMember(String id);

    List<MemberResponse> getMembers();

    MemberResponse getMemberDeleted(String id);
}