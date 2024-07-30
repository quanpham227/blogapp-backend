package com.pivinadanang.blog.repositories;

import com.pivinadanang.blog.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByEmail(String email);
    //SELECT * FROM users WHERE phoneNumber=?
    //query command
    @Query("SELECT o FROM UserEntity o WHERE o.active = true AND (:keyword IS NULL OR :keyword = '' OR " +
            "o.fullName LIKE %:keyword% " +
            "OR o.phoneNumber LIKE %:keyword%) " +
            "AND LOWER(o.role.name) = 'user'")
    Page<UserEntity> findAll(@Param("keyword") String keyword, Pageable pageable);
    List<UserEntity> findByRoleId(Long roleId);

    Optional<UserEntity> findByFacebookAccountId(String facebookAccountId);
    Optional<UserEntity> findByGoogleAccountId(String googleAccountId);
}
