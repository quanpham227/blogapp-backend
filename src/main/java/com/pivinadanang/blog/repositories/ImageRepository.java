package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.models.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    @Query("SELECT i FROM ImageEntity i WHERE (:keyword IS NULL OR :keyword = '' OR i.objectType LIKE %:keyword%)")
    Page<ImageEntity> searchImages(@Param("keyword") String keyword, Pageable pageable);


}
