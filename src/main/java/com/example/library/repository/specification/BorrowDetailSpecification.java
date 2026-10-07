package com.example.library.repository.specification;

import com.example.library.dto.book.request.BorrowDetailFilter;
import com.example.library.entity.BorrowDetail;
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

    private static Specification<BorrowDetail> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }

    private static Specification<BorrowDetail> hasStatus(BorrowStatus status) {

        if (status == null) {
            return null;
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }

    private static Specification<BorrowDetail> hasKeyword(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        String value = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, criteriaBuilder) -> {

            Join<Object, Object> book =
                    root.join("book", JoinType.INNER);

            Join<Object, Object> borrowRecord =
                    root.join("borrowRecord", JoinType.INNER);

            Join<Object, Object> member =
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

        if (year == null) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {

            Join<Object, Object> borrowRecord =
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