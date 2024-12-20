package com.pivinadanang.blog.responses.user;


import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.UserEntity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseTest {

    @Test
    void fromUser_shouldMapUserEntityToUserResponse() {
        // Mock dữ liệu UserEntity
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName("ADMIN");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("123456789");
        user.setProfileImage("profile.jpg");
        user.setActive(true);
        user.setFacebookAccountId("fb123");
        user.setGoogleAccountId("google123");
        user.setRole(role);

        // Thực thi phương thức fromUser
        UserResponse userResponse = UserResponse.fromUser(user);

        // Kiểm tra kết quả
        assertNotNull(userResponse);
        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getFullName(), userResponse.getFullName());
        assertEquals(user.getEmail(), userResponse.getEmail());
        assertEquals(user.getPhoneNumber(), userResponse.getPhoneNumber());
        assertEquals(user.getProfileImage(), userResponse.getProfileImage());
        assertEquals(user.isActive(), userResponse.isActive());
        assertEquals(user.getFacebookAccountId(), userResponse.getFacebookAccountId());
        assertEquals(user.getGoogleAccountId(), userResponse.getGoogleAccountId());
        assertNotNull(userResponse.getRole());
        assertEquals(role.getId(), userResponse.getRole().getId());
        assertEquals(role.getName(), userResponse.getRole().getName());
    }
}

