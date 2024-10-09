package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.FavouriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<FavouriteEntity, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long productId);
    FavouriteEntity findByUserIdAndPostId(Long userId, Long prostId);

    Long countByPostId(Long postId);
}

