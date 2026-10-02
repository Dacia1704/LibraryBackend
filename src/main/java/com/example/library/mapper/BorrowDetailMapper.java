package com.example.library.mapper;

import com.example.library.dto.book.response.BorrowDetailResponse;
import com.example.library.entity.BorrowDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = BookMapper.class
)
public interface BorrowDetailMapper {

    @Mapping(
            target = "bookResponse",
            source = "book"
    )
    BorrowDetailResponse toBorrowDetailResponse(
            BorrowDetail borrowDetail
    );
}