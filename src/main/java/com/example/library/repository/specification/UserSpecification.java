package com.example.library.repository.specification;

import com.example.library.dto.user.request.UserFilter;
import com.example.library.entity.Member;
import com.example.library.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> filter(UserFilter filter) {

        return Specification
                .where(isNotDeleted())
                .and(hasKeyword(filter.getKeyword()))
                .and(hasEmail(filter.getEmail()))
                .and(hasRoleId(filter.getRoleId()))
                .and(hasIsMember(filter.getIsMember()))
                .and(hasPhone(filter.getPhone()));
    }

    private static Specification<User> isNotDeleted() {
        return (root, query, cb) ->
                cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<User> hasKeyword(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        String value = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) ->
                cb.or(
                        cb.like(cb.lower(root.get("username")), value),
                        cb.like(cb.lower(root.get("fullName")), value),
                        cb.like(cb.lower(root.get("noAccent")), value)
                );
    }

    private static Specification<User> hasEmail(String email) {

        if (email == null || email.isBlank()) {
            return null;
        }

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("email")),
                        "%" + email.trim().toLowerCase() + "%"
                );
    }

    private static Specification<User> hasRoleId(Long roleId) {

        if (roleId == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("role").get("id"),
                        roleId
                );
    }

    private static Specification<User> hasIsMember(Boolean isMember) {

        if (isMember == null) {
            return null;
        }

        return (root, query, cb) -> {

            if (Boolean.TRUE.equals(isMember)) {
                return cb.isNotNull(
                        root.get("member").get("id")
                );
            }

            return cb.isNull(
                    root.get("member").get("id")
            );
        };
    }

    private static Specification<User> hasPhone(String phone) {

        if (phone == null || phone.isBlank()) {
            return null;
        }

        return (root, query, cb) -> {

            Join<User, Member> member =
                    root.join("member", JoinType.INNER);

            return cb.like(
                    member.get("phone"),
                    "%" + phone.trim() + "%"
            );
        };
    }
}