package com.example.library.dto.user.request;

import com.example.library.entity.enums.CardStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MemberRequest {

    @Size(max = 12)
    private String identityNumber;

    @Size(max = 15)
    private String phone;

    private CardStatus cardStatus;

    @Size(max = 255)
    private String address;
}