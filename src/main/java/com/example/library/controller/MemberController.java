package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.user.request.MemberRequest;
import com.example.library.dto.user.response.MemberResponse;
import com.example.library.service.MemberService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberController {

    MemberService memberService;

    @PostMapping
    public ApiResponse<MemberResponse> createMember(
            @RequestBody @Valid MemberRequest request
    ) {
        return ApiResponse.success(
                memberService.createMember(request)
        );
    }

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public ApiResponse<MemberResponse> deleteMember(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                memberService.deleteMember(id)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> getMember(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                memberService.getMember(id)
        );
    }

    @GetMapping
    public ApiResponse<List<MemberResponse>> getMembers() {
        return ApiResponse.success(
                memberService.getMembers()
        );
    }

    @GetMapping("/deleted/{id}")
    public ApiResponse<MemberResponse> getMemberDeleted(
            @PathVariable String id
    ) {
        return ApiResponse.success(
                memberService.getMemberDeleted(id)
        );
    }
}