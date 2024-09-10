package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.models.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByUserIdAndPostId(@Param("userId") Long userId,
                                           @Param("postId") Long postId);
    List<CommentEntity> findByPostId(@Param("postId") Long postId);


}