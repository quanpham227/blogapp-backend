package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.responses.category.CategoryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM CategoryEntity c WHERE c.name = :name")
    boolean exitstsByName(String name);

    @Query("SELECT c FROM CategoryEntity c LEFT JOIN FETCH c.posts ORDER BY SIZE(c.posts) DESC")
    List<CategoryEntity> findTopCategoriesByPostCount(Pageable pageable);

    @Query("SELECT new com.pivinadanang.blog.responses.category.CategoryResponse(c.id, c.name, c.code, c.description, COUNT(p)) " +
            "FROM CategoryEntity c LEFT JOIN PostEntity p ON p.category.id = c.id AND p.status = :status AND p.visibility = :visibility " +
            "GROUP BY c.id")
    List<CategoryResponse> findCategoriesWithPostCount( @Param("status") PostStatus status,
                                                        @Param("visibility") PostVisibility visibility);

}
