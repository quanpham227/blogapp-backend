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
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String email);

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    //SELECT * FROM users WHERE phoneNumber=?
    //query command
    @Query("SELECT u FROM UserEntity u WHERE " +
            "(:keyword IS NULL OR u.fullName LIKE %:keyword% OR u.email LIKE %:keyword% OR u.facebookAccountId LIKE %:keyword% OR u.googleAccountId LIKE %:keyword%) AND " +
            "(:status IS NULL OR u.active = :status) AND " +
            "(:roleId = 0 OR u.role.id = :roleId)")
    Page<UserEntity> findAll(@Param("keyword") String keyword,
                             @Param("status") Boolean status,
                             @Param("roleId") Long roleId,
                             Pageable pageable);


    List<UserEntity> findByRoleId(Long roleId);

    Optional<UserEntity> findByFacebookAccountId(String facebookAccountId);
    Optional<UserEntity> findByGoogleAccountId(String googleAccountId);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.active = true")
    Long countActiveUsers();

}

