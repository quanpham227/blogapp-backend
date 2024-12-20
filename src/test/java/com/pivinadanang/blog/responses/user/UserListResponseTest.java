package com.pivinadanang.blog.responses.user;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserListResponseTest {

    @Test
    void userListResponse_shouldHoldCorrectValues() {
        // Mock danh sách UserResponse
        UserResponse user1 = new UserResponse(1L, "John Doe", "john@example.com", "123456789",
                "profile.jpg", true, "fb123", "google123", null);
        UserResponse user2 = new UserResponse(2L, "Jane Doe", "jane@example.com", "987654321",
                "profile2.jpg", false, "fb456", "google456", null);

        List<UserResponse> users = Arrays.asList(user1, user2);

        // Tạo UserListResponse
        UserListResponse userListResponse = UserListResponse.builder()
                .users(users)
                .totalPages(5)
                .build();

        // Kiểm tra kết quả
        assertNotNull(userListResponse);
        assertEquals(2, userListResponse.getUsers().size());
        assertEquals(5, userListResponse.getTotalPages());

        // Kiểm tra giá trị của từng UserResponse
        assertEquals("John Doe", userListResponse.getUsers().get(0).getFullName());
        assertEquals("Jane Doe", userListResponse.getUsers().get(1).getFullName());
    }
}
