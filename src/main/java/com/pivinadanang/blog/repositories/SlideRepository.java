package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.SlideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlideRepository extends JpaRepository<SlideEntity, Long> {
    boolean existsByTitle(String title);

    @Query("SELECT COALESCE(MAX(s.order), 0) FROM SlideEntity s")
    Integer findMaxOrder();

    @Modifying
    @Query("UPDATE SlideEntity s SET s.order = s.order + 1 WHERE s.order >= :order")
    void incrementOrderFrom(@Param("order") Integer order);

    @Modifying
    @Query("UPDATE SlideEntity s SET s.order = s.order + 1 WHERE s.order >= :newOrder AND s.order < :oldOrder")
    void updateOrderOnEdit(@Param("oldOrder") Integer oldOrder, @Param("newOrder") Integer newOrder);

    @Modifying
    @Query("UPDATE SlideEntity s SET s.order = s.order - 1 WHERE s.order > :order")
    void decrementOrderAfterDelete(@Param("order") Integer order);

    @Modifying
    @Query("UPDATE SlideEntity s SET s.order = s.order - 1 WHERE s.order > :startOrder AND s.order <= :endOrder")
    void decrementOrderBetween(@Param("startOrder") Integer startOrder, @Param("endOrder") Integer endOrder);


    @Modifying
    @Query("UPDATE SlideEntity s SET s.order = s.order + 1 WHERE s.order >= :startOrder AND s.order < :endOrder")
    void incrementOrderBetween(@Param("startOrder") Integer startOrder, @Param("endOrder") Integer endOrder);
}
