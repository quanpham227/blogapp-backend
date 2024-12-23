package com.pivinadanang.blog.services.user;

import com.pivinadanang.blog.dtos.UpdateUserByAdminDTO;
import com.pivinadanang.blog.dtos.UpdateUserDTO;
import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.dtos.UserLoginDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.InvalidPasswordException;
import com.pivinadanang.blog.exceptions.PermissionDenyException;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IUserService {
    UserEntity createUser(UserDTO userDTO) throws Exception;
    UserEntity getUserDetailsFromToken(String token) throws Exception;
    UserEntity getUserDetailsFromRefreshToken(String token) throws Exception;
    String login(UserLoginDTO userLoginDTO ) throws Exception;
    UserEntity updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;
    Page<UserEntity> findAll(String keyword,Boolean status, Long roleId, Pageable pageable) throws Exception;
    void resetPassword(Long userId, String newPassword) throws InvalidPasswordException, DataNotFoundException, PermissionDenyException;
    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
    UserResponse updateUserByAdmin(Long userId, UpdateUserByAdminDTO updateUserByAdminDTO) throws Exception;
    UserEntity getUserById(Long userId) throws DataNotFoundException;
    String loginSocial(UserLoginDTO userLoginDTO) throws Exception;
    void deleteUser(Long userId) throws Exception;
}
