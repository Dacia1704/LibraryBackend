package com.example.library.repository.specification;

import com.example.library.dto.book.request.BorrowRecordFilter;
import com.example.library.dto.book.request.enum_request.BorrowRecordStatus;
import com.example.library.entity.BorrowDetail;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.enums.BorrowStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordSpecification {

    private BorrowRecordSpecification() {
    }

    public static Specification<BorrowRecord> filter(BorrowRecordFilter filter) {

        return Specification
                .where(isNotDeleted())
                .and(hasMemberKeyword(filter.getMemberKeyword()))
                .and(hasBookKeyword(filter.getBookKeyword()))
                .and(hasBorrowDateRange(
                        filter.getStartBorrowDate(),
                        filter.getEndBorrowDate()
                ))
                .and(hasStatus(filter.getStatus()));
    }

    /**
     * Chỉ lấy BorrowRecord chưa bị xóa.
     */
    private static Specification<BorrowRecord> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    /**
     * Tìm kiếm theo thông tin thành viên.
     *
     * Các trường tìm kiếm:
     * - Họ tên
     * - Tên không dấu
     * - Mã thành viên
     * - CCCD/CMND
     * - Số điện thoại
     * - Email
     */
    private static Specification<BorrowRecord> hasMemberKeyword(
            String memberKeyword
    ) {
        if (memberKeyword == null || memberKeyword.isBlank()) {
            return Specification.unrestricted();
        }

        String value = "%" + memberKeyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> {

            Join<Object, Object> memberJoin =
                    root.join("member", JoinType.INNER);

            Join<Object, Object> userJoin =
                    memberJoin.join("user", JoinType.INNER);

            query.distinct(true);

            return cb.or(
                    cb.like(
                            cb.lower(userJoin.get("fullName")),
                            value
                    ),
                    cb.like(
                            cb.lower(userJoin.get("noAccent")),
                            value
                    ),
                    cb.like(
                            cb.lower(memberJoin.get("memberCode")),
                            value
                    ),
                    cb.like(
                            cb.lower(memberJoin.get("identityNumber")),
                            value
                    ),
                    cb.like(
                            cb.lower(memberJoin.get("phone")),
                            value
                    ),
                    cb.like(
                            cb.lower(userJoin.get("email")),
                            value
                    )
            );
        };
    }

    /**
     * Tìm kiếm theo thông tin sách.
     *
     * Các trường tìm kiếm:
     * - Tên sách
     * - Mã sách
     */
    private static Specification<BorrowRecord> hasBookKeyword(
            String bookKeyword
    ) {
        if (bookKeyword == null || bookKeyword.isBlank()) {
            return Specification.unrestricted();
        }

        String value = "%" + bookKeyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> {

            Join<Object, Object> borrowDetails =
                    root.join("borrowDetails", JoinType.INNER);

            Join<Object, Object> bookJoin =
                    borrowDetails.join("book", JoinType.INNER);

            query.distinct(true);

            return cb.or(
                    cb.like(
                            cb.lower(bookJoin.get("title")),
                            value
                    ),
                    cb.like(
                            cb.lower(bookJoin.get("bookCode")),
                            value
                    )
            );
        };
    }

    /**
     * Lọc theo khoảng ngày mượn.
     *
     * Có thể truyền:
     * - Chỉ startBorrowDate
     * - Chỉ endBorrowDate
     * - Cả hai
     */
    private static Specification<BorrowRecord> hasBorrowDateRange(
            LocalDate startBorrowDate,
            LocalDate endBorrowDate
    ) {
        if (startBorrowDate == null && endBorrowDate == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (startBorrowDate != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("borrowDate"),
                                startBorrowDate
                        )
                );
            }

            if (endBorrowDate != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("borrowDate"),
                                endBorrowDate
                        )
                );
            }

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }

    /**
     * Lọc theo trạng thái phiếu mượn.
     *
     * BORROWING:
     * - Có ít nhất một BorrowDetail đang BORROWING
     * - Chưa quá hạn
     *
     * RETURNED:
     * - Không còn BorrowDetail nào đang BORROWING
     *
     * OVERDUE:
     * - Có ít nhất một BorrowDetail đang BORROWING
     * - Đã quá hạn
     */
    private static Specification<BorrowRecord> hasStatus(
            BorrowRecordStatus status
    ) {

        if (status == null || status == BorrowRecordStatus.ALL) {
            return Specification.unrestricted();
        }

        return switch (status) {

            /**
             * Đang mượn và chưa quá hạn.
             */
            case BORROWING -> (root, query, cb) -> {

                Join<Object, Object> borrowDetails =
                        root.join(
                                "borrowDetails",
                                JoinType.INNER
                        );

                query.distinct(true);

                LocalDate today = LocalDate.now();

                return cb.and(
                        cb.isFalse(
                                borrowDetails.get("isDeleted")
                        ),
                        cb.equal(
                                borrowDetails.get("status"),
                                BorrowStatus.BORROWING
                        ),
                        cb.greaterThanOrEqualTo(
                                root.get("dueDate"),
                                today
                        )
                );
            };

            /**
             * Đã trả hết.
             *
             * Không tồn tại BorrowDetail chưa xóa
             * có status = BORROWING.
             */
            case RETURNED -> (root, query, cb) -> {

                query.distinct(true);

                Subquery<Long> subquery =
                        query.subquery(Long.class);

                Root<BorrowDetail> detailRoot =
                        subquery.from(BorrowDetail.class);

                subquery.select(
                        cb.literal(1L)
                );

                subquery.where(
                        cb.and(
                                cb.equal(
                                        detailRoot.get("borrowRecord"),
                                        root
                                ),
                                cb.isFalse(
                                        detailRoot.get("isDeleted")
                                ),
                                cb.equal(
                                        detailRoot.get("status"),
                                        BorrowStatus.BORROWING
                                )
                        )
                );

                return cb.not(
                        cb.exists(subquery)
                );
            };

            /**
             * Đang mượn nhưng đã quá hạn.
             */
            case OVERDUE -> (root, query, cb) -> {

                Join<Object, Object> borrowDetails =
                        root.join(
                                "borrowDetails",
                                JoinType.INNER
                        );

                query.distinct(true);

                LocalDate today = LocalDate.now();

                return cb.and(
                        cb.isFalse(
                                borrowDetails.get("isDeleted")
                        ),
                        cb.equal(
                                borrowDetails.get("status"),
                                BorrowStatus.BORROWING
                        ),
                        cb.lessThan(
                                root.get("dueDate"),
                                today
                        )
                );
            };

            /**
             * Trường hợp mặc định.
             */
            default -> Specification.unrestricted();
        };
    }
}