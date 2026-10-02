package com.example.library.repository.specification;

import com.example.library.dto.book.request.BorrowRecordFilter;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.enums.BorrowStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class BorrowRecordSpecification {

    private BorrowRecordSpecification() {
    }

    public static Specification<BorrowRecord> filter(BorrowRecordFilter filter) {

        return Specification
                .where(isNotDeleted())
                .and(hasMemberId(filter.getMemberId()))
                .and(hasBorrowDate(filter.getBorrowDate()))
                .and(hasStatus(filter.getStatus()))
                .and(hasBookId(filter.getBookId()));
    }

    private static Specification<BorrowRecord> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    private static Specification<BorrowRecord> hasMemberId(Long memberId) {

        if (memberId == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("member").get("id"),
                        memberId
                );
    }

    private static Specification<BorrowRecord> hasBorrowDate(java.time.LocalDate borrowDate) {

        if (borrowDate == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("borrowDate"),
                        borrowDate
                );
    }

    private static Specification<BorrowRecord> hasStatus(BorrowStatus status) {

        if (status == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }

    private static Specification<BorrowRecord> hasBookId(Long bookId) {

        if (bookId == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {

            Join<Object, Object> borrowDetails =
                    root.join("borrowDetails", JoinType.INNER);

            return criteriaBuilder.equal(
                    borrowDetails.get("book").get("id"),
                    bookId
            );
        };
    }
}