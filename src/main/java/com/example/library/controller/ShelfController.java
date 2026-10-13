package com.example.library.controller;

import com.example.library.common.ApiResponse;
import com.example.library.dto.book_info.request.ShelfRequest;
import com.example.library.dto.book_info.response.ShelfResponse;
import com.example.library.service.ShelfService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShelfController {

    ShelfService shelfService;

    @PostMapping
    @PreAuthorize("hasAuthority('SHELF_WRITE')")
    public ApiResponse<ShelfResponse> createShelf(
            @RequestBody @Valid ShelfRequest request
    ) {
        return ApiResponse.success(
                shelfService.create(request)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SHELF_READ')")
    public ApiResponse<ShelfResponse> getShelfById(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                shelfService.getById(id)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SHELF_READ')")
    public ApiResponse<List<ShelfResponse>> getAllShelves() {
        return ApiResponse.success(
                shelfService.getAll()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SHELF_WRITE')")
    public ApiResponse<ShelfResponse> updateShelf(
            @PathVariable Long id,
            @RequestBody @Valid ShelfRequest request
    ) {
        return ApiResponse.success(
                shelfService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SHELF_DELETE')")
    public ApiResponse<String> deleteShelf(
            @PathVariable Long id
    ) {
        shelfService.delete(id);
        return ApiResponse.success("Xóa kệ thành công");
    }
}
