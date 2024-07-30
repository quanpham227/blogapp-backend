package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.PostImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostImageRepository extends JpaRepository<PostImageEntity, Long> {
}
