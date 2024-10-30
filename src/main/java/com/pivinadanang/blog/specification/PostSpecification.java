package com.pivinadanang.blog.specification;


import org.springframework.data.jpa.domain.Specification;
import com.pivinadanang.blog.models.PostEntity;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;

public class PostSpecification {

    public static Specification<PostEntity> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<PostEntity> hasCategorySlug(String categorySlug) {
        return (root, query, criteriaBuilder) -> {
            if (categorySlug == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("category").get("code"), categorySlug);
        };
    }

    public static Specification<PostEntity> hasTagSlug(String tagSlug) {
        return (root, query, criteriaBuilder) -> {
            if (tagSlug == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.join("tags").get("slug"), tagSlug);
        };
    }

    public static Specification<PostEntity> hasStatus(PostStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<PostEntity> hasVisibility(PostVisibility visibility) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("visibility"), visibility);
    }
}