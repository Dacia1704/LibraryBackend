package com.example.library.mapper;

import com.example.library.dto.user.request.MemberCreateRequest;
import com.example.library.dto.user.request.MemberRequest;
import com.example.library.dto.user.response.MemberResponse;
import com.example.library.dto.user.response.UserResponse;
import com.example.library.entity.Member;
import com.example.library.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    Member toMember(MemberRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "memberCode", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    Member toMember(MemberCreateRequest request);

    @Mapping(target = "identityNumber", source = "identityNumber")
    @Mapping(target = "cardStatus", source = "cardStatus")
    @Mapping(target = "user", source = "user", qualifiedByName = "toUserResponseForMember")
    MemberResponse toMemberResponse(Member member);

    /**
     * Dùng khi map Member -> MemberResponse từ phía User.
     * Bỏ qua user để không tạo vòng lặp User -> Member -> User.
     */
    @Named("toMemberResponseForUser")
    @Mapping(target = "identityNumber", source = "identityNumber")
    @Mapping(target = "cardStatus", source = "cardStatus")
    @Mapping(target = "user", ignore = true)
    MemberResponse toMemberResponseForUser(Member member);

    /**
     * Dùng khi map User -> UserResponse từ phía Member.
     * Bỏ qua member để không tạo vòng lặp Member -> User -> Member.
     */
    @Named("toUserResponseForMember")
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "member", ignore = true)
    UserResponse toUserResponseForMember(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    void updateMember(
            @MappingTarget Member member,
            MemberRequest request
    );
}