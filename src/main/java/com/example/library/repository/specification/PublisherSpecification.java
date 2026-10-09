package com.example.library.repository.specification;

import com.example.library.entity.Publisher;
import org.springframework.data.jpa.domain.Specification;

public class PublisherSpecification {

    private PublisherSpecification() {
    }

    public static Specification<Publisher> filter(String keyword) {

        return Specification
                .where(isNotDeleted())
                .and(hasKeyword(keyword));
    }

    private static Specification<Publisher> isNotDeleted() {
        return (root, query, cb) ->
                cb.isFalse(root.get("isDeleted"));
    }

    private static Specification<Publisher> hasKeyword(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }

        String value = "%" + keyword.trim().toLowerCase() + "%";

        return (root, query, cb) ->
                cb.or(
                        cb.like(
                                cb.lower(root.get("name")),
                                value
                        ),
                        cb.like(
                                cb.lower(root.get("noAccent")),
                                value
                        )
                );
    }
}