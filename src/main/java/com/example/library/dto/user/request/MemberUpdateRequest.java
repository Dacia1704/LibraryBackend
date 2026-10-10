package com.example.library.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateRequest {

    // Member fields (excluding userId, memberCode, cardExpiry as they should not be updated)
    @Size(max = 12)
    private String identityNumber;

    @Size(max = 15)
    private String phone;

    private com.example.library.entity.enums.CardStatus cardStatus;

    @Size(max = 255)
    private String address;

    // User fields linked to this member
    @Size(max = 50)
    private String username;

    @Size(min = 6, max = 100)
    private String password;

    @Size(max = 100)
    private String fullName;

    @Email
    @Size(max = 100)
    private String email;

    private String avatar;

    private Long roleId;

    private Boolean isActive;
}
