package com.example.library.service;

import com.example.library.common.PageResponse;
import com.example.library.dto.user.request.UserActiveRequest;
import com.example.library.dto.user.request.UserFilter;
import com.example.library.dto.user.request.UserRequest;
import com.example.library.dto.user.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(Long id, UserRequest request);

    UserResponse deleteUser(String id);

    UserResponse getUser(String id);

    PageResponse<UserResponse> getUsers(
            UserFilter filter,
            int page,
            int size
    );

    UserResponse getUserDeleted(String id);

    UserResponse setUserActive(Long id, UserActiveRequest request);
}