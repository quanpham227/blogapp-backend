package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByUserIdAndPostId(@Param("userId") Long userId,
                                           @Param("postId") Long postId);
    List<CommentEntity> findByPostId(@Param("postId") Long postId);

    Long countByPostId(Long postId);

}