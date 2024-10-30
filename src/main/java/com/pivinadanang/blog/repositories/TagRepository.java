package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.TagEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByName(String name);

    @Query("SELECT t FROM TagEntity t ORDER BY SIZE(t.posts) DESC")
    List<TagEntity> findTopTags(Pageable pageable);
}
