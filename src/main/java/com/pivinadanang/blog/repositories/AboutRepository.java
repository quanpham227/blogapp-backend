package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.AboutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AboutRepository extends JpaRepository<AboutEntity, Long> {
    Optional<AboutEntity> findByUniqueKey(String uniqueKey);
}
