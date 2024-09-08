package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.SlideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlideRepository extends JpaRepository<SlideEntity, Long> {

}
