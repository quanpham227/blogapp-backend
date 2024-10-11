package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.models.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    boolean existsByTitle(String title);

    List<PostEntity> findByCategory(CategoryEntity category);






    @Query("SELECT p FROM PostEntity p WHERE "
            + "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR :keyword IS NULL) "
            + "AND (:categoryId = 0 OR p.category.id = :categoryId) "
            + "AND (:status IS NULL OR p.status = :status) "
            + "AND (:startDate IS NULL OR p.createdAt >= :startDate) "
            + "AND (:endDate IS NULL OR p.createdAt <= :endDate)"
            + "ORDER BY p.priority DESC")
    Page<PostEntity> searchPosts(@Param("categoryId") Long categoryId,
                                 @Param("keyword") String keyword,
                                 @Param("status") PostStatus status,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 Pageable pageable);




    @Query("SELECT p FROM PostEntity p " +
            "LEFT JOIN FETCH p.category c " +
            "WHERE p.id = :postId")
    Optional<PostEntity> findPostById(@Param("postId") Long postId);

    @Query("SELECT p FROM PostEntity p WHERE p.slug = :slug")
    Optional<PostEntity> findPostsBySlug(@Param("slug") String slug);

    @Query(value = "SELECT * FROM posts ORDER BY created_at DESC LIMIT ?1", nativeQuery = true)
    List<PostEntity> findTopNRecentPosts(int limit);


    @Query("SELECT p.createdAt FROM PostEntity p")
    List<LocalDateTime> findAllCreatedAt();

    Long countByStatus(PostStatus status);

    long count ();

    @Query("SELECT COALESCE(MAX(p.priority), 0) FROM PostEntity p")
    int findMaxPriority();
}
