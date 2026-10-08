package com.example.library.repository.specification;

import com.example.library.dto.user.request.MemberFilter;
import com.example.library.entity.Member;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecification {

    private MemberSpecification() {
    }

    public static Specification<Member> filter(MemberFilter filter) {
        return Specification
                .where(isNotDeleted())
                .and(hasKeyword(filter.getKeyword()));
    }

    private static Specification<Member> isNotDeleted() {
        return (root, query, cb) ->
                cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<Member> hasKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }

        String value = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) -> {
            Join<?, ?> userJoin = root.join("user", JoinType.INNER);

            return cb.or(
                    cb.like(cb.lower(root.get("memberCode")), value),
                    cb.like(cb.lower(userJoin.get("fullName")), value),
                    cb.like(cb.lower(userJoin.get("noAccent")), value)
            );
        };
    }
}