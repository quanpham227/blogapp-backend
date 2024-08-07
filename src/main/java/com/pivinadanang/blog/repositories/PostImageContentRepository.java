package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.PostImageContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostImageContentRepository extends JpaRepository<PostImageContent, Long> {

    @Query ("DELETE FROM PostImageContent p WHERE p.post.id = :postId")
    void deleteByPostId(Long postId);

    @Query ("SELECT p FROM PostImageContent p WHERE p.post.id = :postId")
    Optional<PostImageContent> findByPostId(Long postId);

    @Query ("SELECT p FROM PostImageContent p WHERE p.post.id = :postId")
    List<PostImageContent> findAllByPostId(Long postId);

    @Query ("SELECT p FROM PostImageContent p WHERE p.fileId = :fileId")
    Optional<PostImageContent> findByFileId(String fileId);
}
