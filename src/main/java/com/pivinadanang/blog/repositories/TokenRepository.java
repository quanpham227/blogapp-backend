package com.pivinadanang.blog.repositories;


import com.pivinadanang.blog.models.Token;
import com.pivinadanang.blog.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUser(UserEntity user);
    Token findByToken(String token);
    Token findByRefreshToken(String token);
}
