package com.pivinadanang.blog.services.token;


import com.pivinadanang.blog.models.UserEntity;
import org.springframework.stereotype.Service;
@Service
public interface ITokenService {
    void addToken(UserEntity user, String token, boolean isMobileDevice);
}
