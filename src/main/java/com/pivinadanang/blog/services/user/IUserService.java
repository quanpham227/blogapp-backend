package com.pivinadanang.blog.services.user;

import com.pivinadanang.blog.dtos.UpdateUserDTO;
import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.dtos.UserLoginDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.InvalidPasswordException;
import com.pivinadanang.blog.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IUserService {
    UserEntity createUser(UserDTO userDTO) throws Exception;
    UserEntity getUserDetailsFromToken(String token) throws Exception;
    UserEntity getUserDetailsFromRefreshToken(String token) throws Exception;
    String login(UserLoginDTO userLoginDTO ) throws Exception;
    UserEntity updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;


    Page<UserEntity> findAll(String keyword, Pageable pageable) throws Exception;
    void resetPassword(Long userId, String newPassword)
            throws InvalidPasswordException, DataNotFoundException;
    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
    void changeProfileImage(Long userId, String imageName) throws Exception;
}
