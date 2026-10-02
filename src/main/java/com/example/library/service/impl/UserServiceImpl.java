package com.example.library.service.impl;

import com.example.library.common.PageResponse;
import com.example.library.dto.user.request.UserFilter;
import com.example.library.dto.user.request.UserRequest;
import com.example.library.dto.user.response.UserResponse;
import com.example.library.entity.Role;
import com.example.library.entity.User;
import com.example.library.exception.AppException;
import com.example.library.exception.ErrorCode;
import com.example.library.mapper.UserMapper;
import com.example.library.repository.RoleRepository;
import com.example.library.repository.UserRepository;
import com.example.library.repository.specification.UserSpecification;
import com.example.library.service.UserService;
import com.example.library.utils.TextUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    RoleRepository roleRepository;

    UserMapper userMapper;

    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {

        // Check username
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new AppException(
                    ErrorCode.USER_EXISTED,
                    String.format(
                            "Username %s đã tồn tại",
                            request.getUsername()
                    )
            );
        }

        // Check email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(
                    ErrorCode.EMAIL_EXISTED,
                    String.format(
                            "Email %s đã tồn tại",
                            request.getEmail()
                    )
            );
        }

        // Tìm role
        Role role = roleRepository
                .findByIdAndIsDeletedFalse(request.getRoleId())
                .orElseThrow(() ->
                        new AppException(ErrorCode.ROLE_NOT_FOUND)
                );

        User user = userMapper.toUser(request);

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setNoAccent(
                TextUtils.removeAccent(request.getFullName())
        );

        user.setRole(role);

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        user.setIsDeleted(false);
        user.setFailedAttempts(0);

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(
            Long id,
            UserRequest request
    ) {

        User user = userRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        // Kiểm tra username
        Optional<User> usernameExist =
                userRepository.findByUsername(
                        request.getUsername()
                );

        if (usernameExist.isPresent()
                && !usernameExist.get().getId().equals(id)) {

            throw new AppException(
                    ErrorCode.USER_EXISTED,
                    String.format(
                            "Username %s đã tồn tại",
                            request.getUsername()
                    )
            );
        }

        // Kiểm tra email
        Optional<User> emailExist =
                userRepository.findByEmail(
                        request.getEmail()
                );

        if (emailExist.isPresent()
                && !emailExist.get().getId().equals(id)) {

            throw new AppException(
                    ErrorCode.EMAIL_EXISTED,
                    String.format(
                            "Email %s đã tồn tại",
                            request.getEmail()
                    )
            );
        }

        Role role = roleRepository
                .findByIdAndIsDeletedFalse(
                        request.getRoleId()
                )
                .orElseThrow(() ->
                        new AppException(ErrorCode.ROLE_NOT_FOUND)
                );

        userMapper.updateUser(user, request);

        user.setNoAccent(
                TextUtils.removeAccent(request.getFullName())
        );

        user.setRole(role);

        // Chỉ đổi password nếu request có password
        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {

            user.setPasswordHash(
                    passwordEncoder.encode(
                            request.getPassword()
                    )
            );
        }

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse deleteUser(String id) {

        User user = userRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        user.setIsDeleted(true);

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(String id) {

        User user = userRepository
                .findByIdAndIsDeletedFalse(
                        Long.valueOf(id)
                )
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getUsers(
            UserFilter filter,
            int page,
            int size
    ) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 10;
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Specification<User> specification =
                UserSpecification.filter(filter);

        Page<User> userPage =
                userRepository.findAll(specification, pageable);

        List<UserResponse> content = userPage.getContent()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();

        return PageResponse.<UserResponse>builder()
                .data(content)
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserDeleted(String id) {

        User user = userRepository
                .findById(Long.valueOf(id))
                .orElseThrow(() ->
                        new AppException(ErrorCode.USER_NOT_FOUND)
                );

        return userMapper.toUserResponse(user);
    }
}