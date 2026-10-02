package com.example.library.repository.specification;

import com.example.library.entity.Author;
import org.springframework.data.jpa.domain.Specification;

public class AuthorSpecification {

    public static Specification<Author> hasKeyword(String keyword) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String value = "%" + keyword.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            value
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("noAccent")),
                            value
                    )
            );
        };
    }

    public static Specification<Author> isNotDeleted() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted"));
    }
}