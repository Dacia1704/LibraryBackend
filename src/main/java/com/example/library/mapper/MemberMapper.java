package com.example.library.mapper;

import com.example.library.dto.user.request.MemberCreateRequest;
import com.example.library.dto.user.request.MemberRequest;
import com.example.library.dto.user.response.MemberResponse;
import com.example.library.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    Member toMember(MemberRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "memberCode", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    Member toMember(MemberCreateRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "fullName", source = "user.fullName")
    MemberResponse toMemberResponse(Member member);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    void updateMember(
            @MappingTarget Member member,
            MemberRequest request
    );
}