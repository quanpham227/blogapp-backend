package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface  CategoryRepository extends JpaRepository<CategoryEntity, Long> {


}
