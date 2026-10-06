package com.example.library.mapper;

import com.example.library.dto.book_info.request.ShelfRequest;
import com.example.library.dto.book_info.response.ShelfResponse;
import com.example.library.entity.Shelf;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ShelfMapper {

    Shelf toEntity(ShelfRequest request);

    ShelfResponse toResponse(Shelf shelf);

    void updateEntity(ShelfRequest request, @MappingTarget Shelf shelf);
}