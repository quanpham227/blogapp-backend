package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.AchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AchievementRepository extends JpaRepository<AchievementEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM AchievementEntity a WHERE a.title = :title AND a.isActive = true")
    boolean exitstsByTitle(String title);

    @Query("SELECT a FROM AchievementEntity a WHERE a.isActive = true")
    List<AchievementEntity> findAllByIsActiveTrue();




}
