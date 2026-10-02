package com.example.library.mapper;

import com.example.library.dto.book.response.FinePaymentResponse;
import com.example.library.entity.FinePayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FinePaymentMapper {

    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "receivedBy", source = "receivedBy.id")
    @Mapping(target = "receivedByUsername", source = "receivedBy.username")
    FinePaymentResponse toResponse(FinePayment finePayment);
}