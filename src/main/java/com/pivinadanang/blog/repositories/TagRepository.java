package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.AboutEntity;
import com.pivinadanang.blog.models.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {
    Optional<TagEntity> findByName(String name);
}
