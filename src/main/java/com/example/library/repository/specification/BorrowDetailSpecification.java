package com.example.library.repository.specification;

import com.example.library.dto.book.request.BorrowDetailFilter;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowDetail;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Member;
import com.example.library.entity.enums.BorrowStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class BorrowDetailSpecification {

    private BorrowDetailSpecification() {
    }

    public static Specification<BorrowDetail> filter(BorrowDetailFilter filter) {

        return Specification
                .where(isNotDeleted())
                .and(hasStatus(filter.getStatus()))
                .and(hasKeyword(filter.getKeyword()))
                .and(hasBorrowYear(filter.getYear()));
    }

    public static Specification<BorrowDetail> belongsToUser(Long userId) {
        return (root, query, criteriaBuilder) -> {

            if (userId == null) {
                return criteriaBuilder.conjunction();
            }

            Join<BorrowDetail, BorrowRecord> borrowRecord =
                    root.join("borrowRecord", JoinType.INNER);

            Join<BorrowRecord, Member> member =
                    borrowRecord.join("member", JoinType.INNER);

            query.distinct(true);

            return criteriaBuilder.equal(
                    member.get("user").get("id"),
                    userId
            );
        };
    }

    private static Specification<BorrowDetail> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    private static Specification<BorrowDetail> hasStatus(BorrowStatus status) {

        return (root, query, criteriaBuilder) -> {

            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(
                    root.get("status"),
                    status
            );
        };
    }

    private static Specification<BorrowDetail> hasKeyword(String keyword) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String value = "%" + keyword.trim().toLowerCase() + "%";

            Join<BorrowDetail, Book> book =
                    root.join("book", JoinType.INNER);

            Join<BorrowDetail, BorrowRecord> borrowRecord =
                    root.join("borrowRecord", JoinType.INNER);

            Join<BorrowRecord, Member> member =
                    borrowRecord.join("member", JoinType.INNER);

            query.distinct(true);

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(book.get("title")),
                            value
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(book.get("noAccent")),
                            value
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(member.get("memberCode")),
                            value
                    )
            );
        };
    }

    private static Specification<BorrowDetail> hasBorrowYear(Integer year) {

        return (root, query, criteriaBuilder) -> {

            if (year == null) {
                return criteriaBuilder.conjunction();
            }

            Join<BorrowDetail, BorrowRecord> borrowRecord =
                    root.join("borrowRecord", JoinType.INNER);

            return criteriaBuilder.equal(
                    criteriaBuilder.function(
                            "YEAR",
                            Integer.class,
                            borrowRecord.get("borrowDate")
                    ),
                    year
            );
        };
    }
}