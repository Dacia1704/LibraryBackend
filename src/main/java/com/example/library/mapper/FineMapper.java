package com.example.library.mapper;

import com.example.library.dto.book.response.FineResponse;
import com.example.library.entity.Fine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {
        BorrowDetailMapper.class
})
public interface FineMapper {

    @Mapping(target = "borrowId", source = "borrowRecord.id")
    @Mapping(target = "borrowDetailId", source = "borrowDetail.id")
    @Mapping(target = "borrowDetail", source = "borrowDetail")
    FineResponse toResponse(Fine fine);
}