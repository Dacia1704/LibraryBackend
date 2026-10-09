package com.example.library.mapper;

import com.example.library.dto.book.response.BorrowDetailResponse;
import com.example.library.dto.book.response.BorrowDetailResponseForRecord;
import com.example.library.entity.BorrowDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {
                BookMapper.class,
                BorrowRecordMapper.class
        }
)
public interface BorrowDetailMapper {

    @Mapping(target = "book", source = "book")
    @Mapping(target = "borrowRecord", source = "borrowRecord")
    @Mapping(target = "borrowStatus", source = "status")
    BorrowDetailResponse toBorrowDetailResponse(BorrowDetail borrowDetail);

    @Named("toBorrowDetailResponseForRecord")
    @Mapping(target = "book", source = "book")
    @Mapping(target = "borrowStatus", source = "status")
    BorrowDetailResponseForRecord toBorrowDetailResponseForRecord(BorrowDetail borrowDetail);
}
