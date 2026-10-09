package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.common.PageResponse;
import com.example.library.dto.user.request.CreateUserMemberRequest;
import com.example.library.dto.user.request.MemberCreateRequest;
import com.example.library.dto.user.request.MemberFilter;
import com.example.library.dto.user.request.MemberRenewRequest;
import com.example.library.dto.user.request.MemberRequest;
import com.example.library.dto.user.response.MemberResponse;
import com.example.library.service.MemberService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberController {

    MemberService memberService;

    @PostMapping
    @PreAuthorize("hasAuthority('MEMBER_WRITE')")
    public ApiResponse<MemberResponse> createMember(
            @RequestBody @Valid MemberCreateRequest request
    ) {
        return ApiResponse.success(
                memberService.createMember(request)
        );
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('MEMBER_WRITE')")
    public ApiResponse<MemberResponse> createUserAndMember(
            @RequestBody @Valid CreateUserMemberRequest request
    ) {
        return ApiResponse.success(
                memberService.createUserAndMember(request)
        );
    }

    @PostMapping("/{id}/renew")
    @PreAuthorize("hasAuthority('MEMBER_WRITE')")
    public ApiResponse<MemberResponse> renewMember(
            @PathVariable Long id,
            @RequestBody @Valid MemberRenewRequest request
    ) {
        return ApiResponse.success(
                memberService.renewMember(id,request)
        );
    }



    @GetMapping("/me")
    public ApiResponse<MemberResponse> getMe() {
        return ApiResponse.success(memberService.getMe());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MEMBER_READ')")
    public ApiResponse<List<MemberResponse>> getMembers(
            @RequestParam(required = false) String keyword
    ) {
        return ApiResponse.success(
                memberService.getMembers(keyword)
        );
    }

    @GetMapping("/pagination")
    @PreAuthorize("hasAuthority('MEMBER_READ')")
    public ApiResponse<PageResponse<MemberResponse>> getPagination(
            @ModelAttribute MemberFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.<PageResponse<MemberResponse>>builder()
                .data(
                        memberService.getMembersPagination(
                                filter,
                                page,
                                size
                        )
                )
                .build();
    }

    @GetMapping("/deleted/{id}")
    @PreAuthorize("hasRole('MEMBER_READ')")
    public ApiResponse<MemberResponse> getMemberDeleted(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                memberService.getMemberDeleted(id)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MEMBER_READ')")
    public ApiResponse<MemberResponse> getMember(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                memberService.getMember(id)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEMBER_WRITE')")
    public ApiResponse<MemberResponse> deleteMember(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                memberService.deleteMember(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEMBER_WRITE')")
    public ApiResponse<MemberResponse> updateMember(
            @PathVariable Long id,
            @RequestBody @Valid MemberRequest request
    ) {
        return ApiResponse.success(
                memberService.updateMember(
                        id,
                        request
                )
        );
    }
}