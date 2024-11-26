package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.enums.CommentStatus;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CommentEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByUserIdAndPostId(@Param("userId") Long userId,
                                           @Param("postId") Long postId);

    // Thêm một phương thức mới trong CommentRepository
    @Query("SELECT c FROM CommentEntity c WHERE c.post.id = :postId AND c.status = :status AND c.parentComment IS NULL ORDER BY c.createdAt DESC ")
    List<CommentEntity> findParentCommentsByPostIdAndStatus(@Param("postId") Long postId, @Param("status") CommentStatus status, Pageable pageable);

    @Query("SELECT r FROM CommentEntity r WHERE r.parentComment.id IN :parentIds AND r.status = :status")
    List<CommentEntity> findRepliesByParentIdsAndStatus(@Param("parentIds") List<Long> parentIds, @Param("status") CommentStatus status);
//
//    @Query("SELECT c FROM CommentEntity c WHERE c.post.id = :postId AND c.status = :status")
//    List<CommentEntity> findByPostIdAndStatus(@Param("postId") Long postId, @Param("status") CommentStatus status);



    List<CommentEntity> findByStatusNot(CommentStatus status);
    Optional<CommentEntity> findByIdAndStatusNot(Long id, CommentStatus status);
    List<CommentEntity> findByParentCommentId(Long parentCommentId);

}