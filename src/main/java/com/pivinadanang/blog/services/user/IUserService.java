package com.pivinadanang.blog.services.user;

import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.dtos.UserLoginDTO;
import com.pivinadanang.blog.models.UserEntity;
import org.springframework.stereotype.Service;


public interface IUserService {
    UserEntity createUser(UserDTO userDTO) throws Exception;
    UserEntity getUserDetailsFromToken(String token) throws Exception;
    String login(String phoneNumber, String password) throws Exception;
}
