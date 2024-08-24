package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface  CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM CategoryEntity c WHERE c.name = :name")
    boolean exitstsByName(String name);
}
