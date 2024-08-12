package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.CategoryEntity;
import com.pivinadanang.blog.models.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {

    boolean existsByTitle(String title);
    Page<PostEntity> findAll(Pageable pageable);//phân trang
    List<PostEntity> findByCategory(CategoryEntity category);

    @Query("SELECT p FROM PostEntity p WHERE " +
            "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId) " +
            "AND (:keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<PostEntity> searchPosts(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT p FROM PostEntity p LEFT JOIN FETCH p.image WHERE p.id = :postId")
    Optional<PostEntity> getDetailPost(@Param("postId") Long postId);

    @Query("SELECT p FROM PostEntity p WHERE p.id IN :postIds")
    List<PostEntity> findPostsByIds(@Param("postIds") List<Long> postIds);

    @Query("SELECT p FROM PostEntity p JOIN p.favorites f WHERE f.user.id = :userId")
    List<PostEntity> findFavoritePostsByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM PostEntity p WHERE p.slug = :slug")
    Optional<PostEntity> findPostBySlug(@Param("slug") String slug);

    @Query(value = "SELECT * FROM posts ORDER BY created_at DESC LIMIT ?1", nativeQuery = true)
    List<PostEntity> findTopNRecentPosts(int limit);

}
