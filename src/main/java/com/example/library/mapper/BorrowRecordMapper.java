package com.example.library.mapper;

import com.example.library.dto.book.response.BorrowRecordResponse;
import com.example.library.entity.BorrowRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {
                BorrowDetailMapper.class,
                MemberMapper.class,
                UserMapper.class
        }
)
public interface BorrowRecordMapper {

    @Mapping(
            target = "member",
            source = "member"
    )
    @Mapping(
            target = "librarian",
            source = "librarian"
    )
    @Mapping(
            target = "borrowDetails",
            source = "borrowDetails"
    )
    BorrowRecordResponse toBorrowRecordResponse(
            BorrowRecord borrowRecord
    );
}