package com.pivinadanang.blog.services.token;


import com.pivinadanang.blog.models.Token;
import com.pivinadanang.blog.models.UserEntity;
import org.springframework.stereotype.Service;
@Service
public interface ITokenService {
    Token addToken(UserEntity user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, UserEntity user) throws Exception;
}
