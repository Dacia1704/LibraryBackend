package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.book.request.SettingRequest;
import com.example.library.dto.book.response.SettingResponse;
import com.example.library.service.SettingService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingController {

    SettingService settingService;

    @PostMapping
    @PreAuthorize("hasAuthority('SETTING_MANAGE')")
    public ApiResponse<SettingResponse> create(
            @RequestBody @Valid SettingRequest request
    ) {

        return ApiResponse.<SettingResponse>builder()
                .data(settingService.create(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SETTING_MANAGE')")
    public ApiResponse<SettingResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid SettingRequest request
    ) {

        return ApiResponse.<SettingResponse>builder()
                .data(settingService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SETTING_MANAGE')")
    public ApiResponse<SettingResponse> delete(
            @PathVariable Long id
    ) {

        return ApiResponse.<SettingResponse>builder()
                .data(settingService.delete(id))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SETTING_MANAGE')")
    public ApiResponse<SettingResponse> getById(
            @PathVariable Long id
    ) {

        return ApiResponse.<SettingResponse>builder()
                .data(settingService.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<List<SettingResponse>> getAll() {

        return ApiResponse.<List<SettingResponse>>builder()
                .data(settingService.getAll())
                .build();
    }
}