package com.example.library.service;

import com.example.library.dto.user.request.UserRequest;
import com.example.library.dto.user.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse updateUser(Long id, UserRequest request);

    UserResponse deleteUser(String id);

    UserResponse getUser(String id);

    List<UserResponse> getUsers();

    UserResponse getUserDeleted(String id);
}