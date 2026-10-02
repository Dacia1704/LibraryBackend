package com.example.library.mapper;

import com.example.library.dto.user.response.MemberPaymentResponse;
import com.example.library.entity.MemberPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberPaymentMapper {

    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "memberCode", source = "member.memberCode")
    @Mapping(target = "memberName", source = "member.user.fullName")
    @Mapping(target = "receivedBy", source = "receivedBy.id")
    @Mapping(target = "receivedByUsername", source = "receivedBy.username")
    MemberPaymentResponse toResponse(MemberPayment memberPayment);
}