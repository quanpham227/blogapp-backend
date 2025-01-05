package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.enums.CommentStatus;
import com.pivinadanang.blog.enums.PostStatus;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CommentEntity;
import com.pivinadanang.blog.models.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByUserIdAndPostId(@Param("userId") Long userId,
                                           @Param("postId") Long postId);

    // Thêm một phương thức mới trong CommentRepository
    @Query("SELECT c FROM CommentEntity c WHERE c.post.id = :postId AND c.status = :status AND c.parentComment IS NULL ORDER BY c.createdAt DESC ")
    List<CommentEntity> findParentCommentsByPostIdAndStatus(@Param("postId") Long postId, @Param("status") CommentStatus status, Pageable pageable);

    @Query("SELECT r FROM CommentEntity r LEFT JOIN FETCH r.parentComment WHERE r.parentComment.id IN :parentIds AND r.status = :status")
    List<CommentEntity> findRepliesByParentIdsAndStatus(@Param("parentIds") List<Long> parentIds, @Param("status") CommentStatus status);

    List<CommentEntity> findByParentCommentId(Long parentCommentId);

    @Query("SELECT c FROM CommentEntity c WHERE "
            + "(LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR :keyword IS NULL) "
            + "AND (:status IS NULL OR c.status = :status) "
            + "AND (c.status != :deletedStatus OR :status = :deletedStatus)")
    Page<CommentEntity> getAllComments(@Param("keyword") String keyword,
                                       @Param("status") CommentStatus status,
                                       @Param("deletedStatus") CommentStatus deletedStatus,
                                       Pageable pageable);


    @Query("SELECT COUNT(c) FROM CommentEntity c WHERE c.createdAt >= :startOfDay AND c.createdAt < :endOfDay")
    Long countTodayComments(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT DATE(c.createdAt) AS day, COUNT(c) AS count " +
            "FROM CommentEntity c " +
            "WHERE c.createdAt >= :startDate AND c.createdAt < :endDate " +
            "GROUP BY DATE(c.createdAt) " +
            "ORDER BY DATE(c.createdAt)")
    List<Object[]> countCommentsPerDayLastWeek(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);


}