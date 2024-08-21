package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  CategoryRepository extends JpaRepository<CategoryEntity, Long> {

}
