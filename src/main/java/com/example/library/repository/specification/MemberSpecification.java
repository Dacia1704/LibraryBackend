package com.example.library.repository.specification;

import com.example.library.dto.user.request.MemberFilter;
import com.example.library.entity.Member;
import com.example.library.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MemberSpecification {

    private MemberSpecification() {
    }

    public static Specification<Member> filter(MemberFilter filter) {
        return Specification
                .where(hasKeyword(filter.getKeyword()))
                .and(hasRole(filter.getRole()))
                .and(hasCard(filter.getHasCard()))
                .and(hasEmail(filter.getEmail()))
                .and(hasPhone(filter.getPhone()));
    }

    private static Specification<Member> isNotDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
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
                    cb.like(cb.lower(userJoin.get("noAccent")), value),
                    cb.like(cb.lower(userJoin.get("username")), value)
            );
        };
    }

    private static Specification<Member> hasRole(String role) {
        if (role == null || role.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }

        return (root, query, cb) -> {
            Join<Member, User> userJoin = root.join("user", JoinType.INNER);
            return cb.equal(cb.lower(userJoin.get("role").get("name")), role.toLowerCase().trim());
        };
    }

    private static Specification<Member> hasCard(Boolean hasCard) {
        if (hasCard == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        return (root, query, cb) -> {
            if (hasCard) {
                return cb.isNotNull(root.get("user"));
            } else {
                return cb.isNull(root.get("user"));
            }
        };
    }

    private static Specification<Member> hasEmail(String email) {
        if (email == null || email.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }

        String value = "%" + email.trim().toLowerCase() + "%";

        return (root, query, cb) -> {
            Join<Member, User> userJoin = root.join("user", JoinType.INNER);
            return cb.like(cb.lower(userJoin.get("email")), value);
        };
    }

    private static Specification<Member> hasPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }

        String value = "%" + phone.trim() + "%";

        return (root, query, cb) -> cb.like(root.get("phone"), value);
    }
}