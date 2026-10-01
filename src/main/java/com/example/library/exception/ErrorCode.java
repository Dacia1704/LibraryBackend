package com.example.library.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Auth & User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Người dùng không tồn tại"),
    USER_EXISTED(HttpStatus.BAD_REQUEST, "Tên đăng nhập hoặc email đã tồn tại"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Email đã tồn tại"),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "Email google chưa được xác minh"),
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED, "Xác minh người dùng thất bại"),
    USER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "Tài khoản người dùng không hoạt động"),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Role không tồn tại"),
    PERMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Quyền ko tồn tại");



    private final HttpStatus code; // Mã HTTP Status
    private final String message; // Lời nhắn hiển thị cho user

    ErrorCode(HttpStatus code, String message) {
        this.code = code;
        this.message = message;
    }

    // (Tuỳ chọn) Hàm hỗ trợ lấy ra mã số nguyên (VD: 400, 404)
    // Dùng cho trường hợp Frontend cần 1 trường "statusCode" là số trong cục JSON trả về
    public int getStatusCodeValue() {
        return this.code.value();
    }
}
