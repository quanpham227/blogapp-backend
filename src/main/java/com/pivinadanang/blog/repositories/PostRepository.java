package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.enums.PostVisibility;
import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.models.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    boolean existsByTitle(String title);

    List<PostEntity> findByCategory(CategoryEntity category);


    @Query("SELECT p FROM PostEntity p " +
            "LEFT JOIN FETCH p.category c " +
            "WHERE p.id = :postId")
    Optional<PostEntity> findPostById(@Param("postId") Long postId);


    @Query("SELECT p.createdAt FROM PostEntity p")
    List<LocalDateTime> findAllCreatedAt();


    long count ();

    @Query("SELECT COALESCE(MAX(p.priority), 0) FROM PostEntity p")
    int findMaxPriority();



    @Query("SELECT p FROM PostEntity p WHERE "
            + "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR :keyword IS NULL) "
            + "AND (:categoryId = 0 OR p.category.id = :categoryId) "
            + "AND (:status IS NULL OR p.status = :status) "
            + "AND (:startDate IS NULL OR p.createdAt >= :startDate) "
            + "AND (:endDate IS NULL OR p.createdAt <= :endDate) "
            + "AND p.status != :deletedStatus " // Loại bỏ các bài viết có trạng thái DELETED
            + "ORDER BY p.priority DESC, p.createdAt DESC")
    Page<PostEntity> searchPostsForAdmin(@Param("categoryId") Long categoryId,
                                         @Param("keyword") String keyword,
                                         @Param("status") PostStatus status,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         @Param("deletedStatus") PostStatus deletedStatus,
                                         Pageable pageable);

    @Query("SELECT p FROM PostEntity p WHERE "
            + "(:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND (:categoryId = 0 OR p.category.id = :categoryId) "
            + "AND (:status IS NULL OR p.status = :status) "
            + "AND (:startDate IS NULL OR p.createdAt >= :startDate) "
            + "AND (:endDate IS NULL OR p.createdAt <= :endDate)"
            + "AND p.status = :deletedStatus "
            + "ORDER BY p.priority DESC")
    Page<PostEntity> searchDeletedPostsForAdmin(@Param("categoryId") Long categoryId,
                                 @Param("keyword") String keyword,
                                 @Param("status") PostStatus status,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 @Param("deletedStatus") PostStatus deletedStatus,
                                 Pageable pageable);

    @Query("SELECT DISTINCT p FROM PostEntity p "
            + "LEFT JOIN FETCH p.category c "
            + "LEFT JOIN FETCH p.tags t "
            + "WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) "
            + "AND (:categorySlug IS NULL OR :categorySlug = '' OR c.code = :categorySlug) "
            + "AND (:tagSlug IS NULL OR :tagSlug = '' OR t.slug = :tagSlug) "
            + "AND p.status = :status "
            + "AND p.visibility = :visibility "
            + "ORDER BY p.priority DESC, p.createdAt DESC")
    Page<PostEntity> searchPostsForUser(
            @Param("keyword") String keyword,
            @Param("categorySlug") String categorySlug,
            @Param("tagSlug") String tagSlug,
            @Param("status") PostStatus status,
            @Param("visibility") PostVisibility visibility,
            Pageable pageable);



    @Query("SELECT p FROM PostEntity p WHERE "
                + " p.status = :status "
                + "AND p.visibility = :visibility "
                + "ORDER BY p.createdAt DESC")
        Page<PostEntity> findRecentPosts(
                @Param("status") PostStatus status,
                @Param("visibility") PostVisibility visibility,
                Pageable pageable);


    Optional<PostEntity> findPostBySlugAndStatusAndVisibility(String slug, PostStatus status, PostVisibility visibility);


    @Query("SELECT p FROM PostEntity p WHERE p.status <> :excludedStatus ORDER BY p.viewCount DESC")
    List<PostEntity> findTop3PostsExcludingStatus(@Param("excludedStatus") PostStatus excludedStatus, Pageable pageable);


    @Query("SELECT SUM(p.viewCount) FROM PostEntity p WHERE p.createdAt >= CURRENT_DATE - 7 GROUP BY FUNCTION('DAY', p.createdAt)")
    List<Long> countPageViewsPerDayLastWeek();
}
