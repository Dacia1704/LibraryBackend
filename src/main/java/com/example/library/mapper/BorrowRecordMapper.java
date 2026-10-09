package com.example.library.mapper;

import com.example.library.dto.book.response.BorrowDetailResponseForRecord;
import com.example.library.dto.book.response.BorrowRecordResponse;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowDetail;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.enums.BorrowStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Mapper giữa BorrowRecord và BorrowRecordResponse.
 *
 * <p>Lưu ý: KHÔNG khai báo {@code BorrowDetailMapper.class} trong {@code uses}
 * để tránh circular dependency giữa hai mapper:
 * <pre>
 *   BorrowRecordMapper ⇄ BorrowDetailMapper
 * </pre>
 * Thay vào đó, việc map danh sách BorrowDetail được thực hiện thủ công
 * trong {@link #mapBorrowDetails(List)} sử dụng BookMapper được inject
 * qua field {@link #bookMapper}.</p>
 */
@Mapper(
        componentModel = "spring",
        uses = {
                MemberMapper.class,
                UserMapper.class
        }
)
public abstract class BorrowRecordMapper {

    @Autowired
    protected BookMapper bookMapper;

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
            expression = "java(mapBorrowDetails(borrowRecord.getBorrowDetails()))"
    )
    public abstract BorrowRecordResponse toBorrowRecordResponse(
            BorrowRecord borrowRecord
    );

    /**
     * Map danh sách BorrowDetail -> BorrowDetailResponseForRecord mà KHÔNG
     * tham chiếu ngược về BorrowRecord (tránh StackOverflow khi mapper cycle).
     * Book được map thông qua BookMapper (inject thủ công qua field).
     */
    protected List<BorrowDetailResponseForRecord> mapBorrowDetails(List<BorrowDetail> borrowDetails) {
        if (borrowDetails == null) {
            return null;
        }
        return borrowDetails.stream()
                .filter(Objects::nonNull)
                .map(this::toBorrowDetailResponseForRecord)
                .toList();
    }

    protected BorrowDetailResponseForRecord toBorrowDetailResponseForRecord(BorrowDetail detail) {
        if (detail == null) {
            return null;
        }
        Book book = detail.getBook();
        return BorrowDetailResponseForRecord.builder()
                .id(detail.getId())
                .book(book == null ? null : bookMapper.toBookResponse(book))
                .returnDate(detail.getReturnDate())
                .fineAmount(detail.getFineAmount() == null ? BigDecimal.ZERO : detail.getFineAmount())
                .borrowStatus(detail.getStatus() == null ? BorrowStatus.BORROWING : detail.getStatus())
                .build();
    }
}
