package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.ImageEntity;
import com.pivinadanang.blog.models.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    @Query("SELECT i FROM ImageEntity i WHERE " +
            "(COALESCE(:objectType, '') = '' OR i.objectType = :objectType) " +
            "AND (COALESCE(:keyword, '') = '' OR LOWER(i.fileName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.objectType) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ImageEntity> searchImages(@Param("keyword") String keyword, @Param("objectType") String objectType, Pageable pageable);

    @Query("SELECT i FROM ImageEntity i WHERE i.usageCount = 0")
    Page<ImageEntity> findUnusedImages(Pageable pageable);


    @Query("SELECT SUM(i.fileSize) FROM ImageEntity i")
    Long getTotalFileSize();

    @Query("SELECT i FROM ImageEntity i WHERE i.publicId = :publicId")
    Optional<ImageEntity> findByPublicId(String publicId);
}