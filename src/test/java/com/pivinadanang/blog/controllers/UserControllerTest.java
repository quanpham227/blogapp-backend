package com.pivinadanang.blog.controllers;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.controller.UserController;
import com.pivinadanang.blog.dtos.UpdateUserByAdminDTO;
import com.pivinadanang.blog.dtos.UpdateUserDTO;
import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.dtos.UserLoginDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.ExpiredTokenException;
import com.pivinadanang.blog.exceptions.PermissionDenyException;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.Token;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.user.UserListResponse;
import com.pivinadanang.blog.responses.user.UserResponse;
import com.pivinadanang.blog.services.auth.AuthService;
import com.pivinadanang.blog.services.token.TokenService;
import com.pivinadanang.blog.services.user.IUserService;
import com.pivinadanang.blog.ultils.MessageKeys;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;




import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
class UserControllerTest {

    @Mock
    private IUserService userService;
    @InjectMocks
    private UserController userController;
    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AuthService authService;
    @Mock
    private TokenService tokenService;


    @Mock
    private LocalizationUtils localizationUtils;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }
    @Test
    void testGetAllUser_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(Collections.singletonList(new UserEntity()));
        when(userService.findAll(anyString(), any(), anyLong(), any(Pageable.class))).thenReturn(userPage);

        ResponseEntity<ResponseObject> response = userController.getAllUser("", null, 0L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).findAll(anyString(), any(), anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllUser_WithKeyword() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(Collections.singletonList(new UserEntity()));
        when(userService.findAll(eq("keyword"), any(), anyLong(), any(Pageable.class))).thenReturn(userPage);

        ResponseEntity<ResponseObject> response = userController.getAllUser("keyword", null, 0L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).findAll(eq("keyword"), any(), anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllUser_WithStatus() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(Collections.singletonList(new UserEntity()));
        when(userService.findAll(anyString(), eq(true), anyLong(), any(Pageable.class))).thenReturn(userPage);

        ResponseEntity<ResponseObject> response = userController.getAllUser("", true, 0L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).findAll(anyString(), eq(true), anyLong(), any(Pageable.class));
    }

    @Test
    void testGetAllUser_WithRoleId() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> userPage = new PageImpl<>(Collections.singletonList(new UserEntity()));
        when(userService.findAll(anyString(), any(), eq(1L), any(Pageable.class))).thenReturn(userPage);

        ResponseEntity<ResponseObject> response = userController.getAllUser("", null, 1L, 0, 10);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).findAll(anyString(), any(), eq(1L), any(Pageable.class));
    }



    @Test
    void testGetAllUser_WithPageAndLimit() throws Exception {
        String keyword = "test";
        Boolean status = true;
        Long roleId = 1L;
        int page = 0;
        int limit = 10;

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").ascending());
        UserEntity userEntity = new UserEntity();
        Page<UserEntity> userPage = new PageImpl<>(Collections.singletonList(userEntity), pageable, 1);

        when(userService.findAll(keyword, status, roleId, pageable)).thenReturn(userPage);

        ResponseEntity<ResponseObject> response = userController.getAllUser(keyword, status, roleId, page, limit);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Get users successfully");
        assertThat(((UserListResponse) response.getBody().getData()).getUsers()).hasSize(1);
    }
    @Test
    void testGetAllUser_EmptyPage() throws Exception {
        String keyword = "test";
        Boolean status = true;
        Long roleId = 1L;
        int page = 0;
        int limit = 10;

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").ascending());
        Page<UserEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(userService.findAll(keyword, status, roleId, pageable)).thenReturn(emptyPage);

        ResponseEntity<ResponseObject> response = userController.getAllUser(keyword, status, roleId, page, limit);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Get users successfully");
        assertThat(((UserListResponse) response.getBody().getData()).getUsers()).isEmpty();
    }

    @Test
    void testGetAllUser_UserNotFound() throws Exception {
        when(userService.findAll(anyString(), any(), anyLong(), any(Pageable.class)))
                .thenThrow(new DataNotFoundException("User not found"));

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                userController.getAllUser("", null, 0L, 0, 10)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userService, times(1)).findAll(anyString(), any(), anyLong(), any(Pageable.class));
    }


    @Test
    void testGetAllUser_InvalidPage() throws Exception {
        ResponseEntity<ResponseObject> response = userController.getAllUser("", null, 0L, -1, 10);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Page index must not be less than zero", response.getBody().getMessage());
    }

    @Test
    void testCreateUser_Success() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .password("password")
                .retypePassword("password")
                .roleId(1L)
                .build();

        UserEntity userEntity = new UserEntity();
        when(userService.createUser(userDTO)).thenReturn(userEntity);
        when(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY)).thenReturn("Register successfully");

        ResponseEntity<ResponseObject> response = userController.createUser(userDTO, bindingResult);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Register successfully", response.getBody().getMessage());
    }



    @Test
    void testCreateUser_InvalidEmail() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .fullName("John Doe")
                .email("invalid-email")
                .phoneNumber("1234567890")
                .password("password")
                .retypePassword("password")
                .roleId(1L)
                .build();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH)).thenReturn("Passwords do not match");

        ResponseEntity<ResponseObject> response = userController.createUser(userDTO, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid email format", response.getBody().getMessage());
    }

    @Test
    void testCreateUser_PasswordMismatch() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .password("password")
                .retypePassword("different-password")
                .roleId(1L)
                .build();

        when(bindingResult.hasErrors()).thenReturn(false);
        when(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH)).thenReturn("Passwords do not match");

        ResponseEntity<ResponseObject> response = userController.createUser(userDTO, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Passwords do not match", response.getBody().getMessage());
    }


    @Test
    void testLogin_Success() throws Exception {
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .email("john.doe@example.com")
                .password("password")
                .build();

        UserEntity userEntity = new UserEntity();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("USER");
        userEntity.setRole(roleEntity);
        Token token = new Token();

        when(userService.login(userLoginDTO)).thenReturn("valid-token");
        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(userEntity);
        when(tokenService.addToken(any(UserEntity.class), eq("valid-token"), anyBoolean())).thenReturn(token);
        when(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY)).thenReturn("Login successfully");
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

        ResponseEntity<ResponseObject> responseEntity = userController.login(userLoginDTO, request, response);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Login successfully", responseEntity.getBody().getMessage());
    }





    @Test
    void testRefreshToken_Success() throws Exception {
        String refreshToken = "valid-refresh-token";
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        UserEntity userEntity = new UserEntity();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("USER");
        userEntity.setRole(roleEntity); // Ensure role is set
        Token jwtToken = new Token();
        jwtToken.setToken("new-token");
        jwtToken.setRefreshToken("new-refresh-token");

        when(userService.getUserDetailsFromRefreshToken(refreshToken)).thenReturn(userEntity);
        when(tokenService.refreshToken(refreshToken, userEntity)).thenReturn(jwtToken);

        try {
            ResponseEntity<ResponseObject> responseEntity = userController.refreshToken(request, response);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertNotNull(responseEntity.getBody());
            assertEquals("Refresh token successfully", responseEntity.getBody().getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            org.junit.jupiter.api.Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }
    @Test
    void testRefreshToken_EmptyRefreshToken() throws Exception {
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<ResponseObject> responseEntity = userController.refreshToken(request, response);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("You are not logged in, please login again", responseEntity.getBody().getMessage());
    }



    @Test
    void testLogout_Success() {
        // Mock the HttpServletResponse
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Call the logout method
        ResponseEntity<ResponseObject> responseEntity = userController.logout(response);

        // Verify that the response status is OK
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Verify that the response body contains the correct message
        assertNotNull(responseEntity.getBody());
        assertEquals("Logged out successfully", responseEntity.getBody().getMessage());

        // Capture the argument passed to addHeader
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response, times(1)).addHeader(eq(HttpHeaders.SET_COOKIE), captor.capture());

        // Assert that the captured argument contains the expected substring
        assertTrue(captor.getValue().contains("refreshToken=;"));
    }
    @Test
    void testGetUserDetails_Success() throws Exception {
        String token = "Bearer valid-token";
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFullName("testuser");

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(userEntity);

        ResponseEntity<ResponseObject> responseEntity = userController.getUserDetails(token);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Get user details successfully", responseEntity.getBody().getMessage());
        assertNotNull(responseEntity.getBody().getData());
    }



    @Test
    void testUpdateUserDetails_Success() throws Exception {
        Long userId = 1L;
        String token = "Bearer valid-token";
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFullName("New Name");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setFullName("Old Name");

        UserEntity updatedUserEntity = new UserEntity();
        updatedUserEntity.setId(userId);
        updatedUserEntity.setFullName("New Name");

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(userEntity);
        when(userService.updateUser(userId, updateUserDTO)).thenReturn(updatedUserEntity);

        ResponseEntity<ResponseObject> responseEntity = userController.updateUserDetails(userId, updateUserDTO, token);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Update user details successfully", responseEntity.getBody().getMessage());
        assertEquals("New Name", ((UserResponse) responseEntity.getBody().getData()).getFullName());
    }

    @Test
    void testUpdateUserDetails_UserIdMismatch() throws Exception {
        Long userId = 1L;
        Long differentUserId = 2L;
        String token = "Bearer valid-token";
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(differentUserId);

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(userEntity);

        ResponseEntity<ResponseObject> responseEntity = userController.updateUserDetails(userId, updateUserDTO, token);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }


    @Test
    void testUpdateUserByAdmin_Success() throws Exception {
        Long userId = 1L;
        String token = "Bearer valid-token";
        UpdateUserByAdminDTO updateUserByAdminDTO = new UpdateUserByAdminDTO();
        updateUserByAdminDTO.setFullName("New Name");

        RoleEntity adminRole = new RoleEntity();
        adminRole.setName(RoleEntity.ADMIN);

        UserEntity requester = new UserEntity();
        requester.setId(2L);
        requester.setRole(adminRole);

        UserEntity targetUser = new UserEntity();
        targetUser.setId(userId);
        targetUser.setFullName("Old Name");
        targetUser.setRole(new RoleEntity(RoleEntity.USER)); // Ensure targetUser has a role

        UserResponse updatedUserResponse = new UserResponse();
        updatedUserResponse.setFullName("New Name");

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(requester);
        when(userService.getUserById(userId)).thenReturn(targetUser);
        when(userService.updateUserByAdmin(userId, updateUserByAdminDTO)).thenReturn(updatedUserResponse);

        ResponseEntity<ResponseObject> responseEntity = userController.updateUserByAdmin(userId, updateUserByAdminDTO, token);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Update user successfully", responseEntity.getBody().getMessage());
        assertEquals("New Name", ((UserResponse) responseEntity.getBody().getData()).getFullName());
    }

    @Test
    void testUpdateUserByAdmin_UpdateSelf() throws Exception {
        Long userId = 1L;
        String token = "Bearer valid-token";
        UpdateUserByAdminDTO updateUserByAdminDTO = new UpdateUserByAdminDTO();
        updateUserByAdminDTO.setFullName("New Name");

        UserEntity requester = new UserEntity();
        requester.setId(userId);
        requester.setRole(new RoleEntity(RoleEntity.ADMIN));

        UserResponse updatedUserResponse = new UserResponse();
        updatedUserResponse.setFullName("New Name");

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(requester);
        when(userService.getUserById(userId)).thenReturn(requester);
        when(userService.updateUserByAdmin(userId, updateUserByAdminDTO)).thenReturn(updatedUserResponse);

        ResponseEntity<ResponseObject> responseEntity = userController.updateUserByAdmin(userId, updateUserByAdminDTO, token);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Update user successfully", responseEntity.getBody().getMessage());
        assertEquals("New Name", ((UserResponse) responseEntity.getBody().getData()).getFullName());
    }


    @Test
    void testUpdateUserByAdmin_UpdateModerator() throws Exception {
        Long userId = 1L;
        String token = "Bearer valid-token";
        UpdateUserByAdminDTO updateUserByAdminDTO = new UpdateUserByAdminDTO();

        UserEntity requester = new UserEntity();
        requester.setId(2L);
        requester.setRole(new RoleEntity(RoleEntity.ADMIN));

        UserEntity targetUser = new UserEntity();
        targetUser.setId(userId);
        targetUser.setRole(new RoleEntity(RoleEntity.MODERATOR));

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(requester);
        when(userService.getUserById(userId)).thenReturn(targetUser);

        ResponseEntity<ResponseObject> responseEntity = userController.updateUserByAdmin(userId, updateUserByAdminDTO, token);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Admins cannot update Moderators", responseEntity.getBody().getMessage());
    }

    @Test
    void testUpdateUserByAdmin_ChangeUserRole() throws Exception {
        Long userId = 1L;
        String token = "Bearer valid-token";
        UpdateUserByAdminDTO updateUserByAdminDTO = new UpdateUserByAdminDTO();
        updateUserByAdminDTO.setRoleId(3L);

        UserEntity requester = new UserEntity();
        requester.setId(2L);
        requester.setRole(new RoleEntity(RoleEntity.ADMIN));

        UserEntity targetUser = new UserEntity();
        targetUser.setId(userId);
        targetUser.setRole(new RoleEntity(RoleEntity.USER));

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(requester);
        when(userService.getUserById(userId)).thenReturn(targetUser);

        ResponseEntity<ResponseObject> responseEntity = userController.updateUserByAdmin(userId, updateUserByAdminDTO, token);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Admins cannot change roles", responseEntity.getBody().getMessage());
    }



    @Test
    void testUpdateUserByAdmin_AccessDenied() throws Exception {
        Long userId = 1L;
        String token = "Bearer valid-token";
        UpdateUserByAdminDTO updateUserByAdminDTO = new UpdateUserByAdminDTO();

        UserEntity requester = new UserEntity();
        requester.setId(2L);
        requester.setRole(new RoleEntity(RoleEntity.USER)); // Not an Admin or Moderator

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(requester);

        ResponseEntity<ResponseObject> responseEntity = userController.updateUserByAdmin(userId, updateUserByAdminDTO, token);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("Access denied", responseEntity.getBody().getMessage());
    }

    @Test
    void testResetPassword_Success() throws Exception {
        long userId = 1L;

        // Capture the generated password
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(userService).resetPassword(eq(userId), passwordCaptor.capture());

        ResponseEntity<ResponseObject> response = userController.resetPassword(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Reset password successfully", response.getBody().getMessage());
        assertEquals(passwordCaptor.getValue(), response.getBody().getData());
    }

    @Test
    void testResetPassword_UserNotFound() throws Exception {
        long userId = 1L;

        doThrow(new DataNotFoundException("User not found")).when(userService).resetPassword(eq(userId), anyString());

        ResponseEntity<ResponseObject> response = userController.resetPassword(userId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User not found", response.getBody().getMessage());
        assertEquals("", response.getBody().getData());
    }

    @Test
    void testResetPassword_Exception() throws Exception {
        long userId = 1L;

        doThrow(new RuntimeException("Unexpected error")).when(userService).resetPassword(eq(userId), anyString());

        ResponseEntity<ResponseObject> response = userController.resetPassword(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unexpected error", response.getBody().getMessage());
        assertEquals("", response.getBody().getData());
    }
    @Test
    void testResetPassword_PermissionDenied() throws Exception {
        long userId = 1L;

        doThrow(new PermissionDenyException("Permission denied")).when(userService).resetPassword(eq(userId), anyString());

        ResponseEntity<ResponseObject> response = userController.resetPassword(userId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Permission denied", response.getBody().getMessage());
        assertEquals("", response.getBody().getData());
    }

    @Test
    void testResetPassword_DataIntegrityViolation() throws Exception {
        long userId = 1L;

        doThrow(new DataIntegrityViolationException("Data integrity violation")).when(userService).resetPassword(eq(userId), anyString());

        ResponseEntity<ResponseObject> response = userController.resetPassword(userId);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Data integrity violation", response.getBody().getMessage());
        assertEquals("", response.getBody().getData());
    }
    @Test
    void testBlockOrEnable_Moderator_Success() throws Exception {
        long userId = 1L;
        int active = 1;
        String token = "Bearer valid-token";

        UserEntity moderator = new UserEntity();
        moderator.setRole(new RoleEntity(RoleEntity.MODERATOR));

        UserEntity targetUser = new UserEntity();
        targetUser.setRole(new RoleEntity(RoleEntity.USER));

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(moderator);
        when(userService.getUserById(userId)).thenReturn(targetUser);

        ResponseEntity<ResponseObject> response = userController.blockOrEnable(userId, active, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully enabled the user.", response.getBody().getMessage());
        verify(userService, times(1)).blockOrEnable(userId, true);
    }

    @Test
    void testBlockOrEnable_Admin_Success() throws Exception {
        long userId = 1L;
        int active = 0;
        String token = "Bearer valid-token";

        UserEntity admin = new UserEntity();
        admin.setRole(new RoleEntity(RoleEntity.ADMIN));

        UserEntity targetUser = new UserEntity();
        targetUser.setRole(new RoleEntity(RoleEntity.USER));

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(admin);
        when(userService.getUserById(userId)).thenReturn(targetUser);

        ResponseEntity<ResponseObject> response = userController.blockOrEnable(userId, active, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully blocked the user.", response.getBody().getMessage());
        verify(userService, times(1)).blockOrEnable(userId, false);
    }

    @Test
    void testBlockOrEnable_Admin_BlockAdmin() throws Exception {
        long userId = 1L;
        int active = 0;
        String token = "Bearer valid-token";

        UserEntity admin = new UserEntity();
        admin.setRole(new RoleEntity(RoleEntity.ADMIN));

        UserEntity targetAdmin = new UserEntity();
        targetAdmin.setRole(new RoleEntity(RoleEntity.ADMIN));

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(admin);
        when(userService.getUserById(userId)).thenReturn(targetAdmin);

        ResponseEntity<ResponseObject> response = userController.blockOrEnable(userId, active, token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Access denied", response.getBody().getMessage());
        verify(userService, never()).blockOrEnable(anyLong(), anyBoolean());
    }

    @Test
    void testBlockOrEnable_Admin_BlockModerator() throws Exception {
        long userId = 1L;
        int active = 0;
        String token = "Bearer valid-token";

        UserEntity admin = new UserEntity();
        admin.setRole(new RoleEntity(RoleEntity.ADMIN));

        UserEntity targetModerator = new UserEntity();
        targetModerator.setRole(new RoleEntity(RoleEntity.MODERATOR));

        when(userService.getUserDetailsFromToken("valid-token")).thenReturn(admin);
        when(userService.getUserById(userId)).thenReturn(targetModerator);

        ResponseEntity<ResponseObject> response = userController.blockOrEnable(userId, active, token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Access denied", response.getBody().getMessage());
        verify(userService, never()).blockOrEnable(anyLong(), anyBoolean());
    }


    @Test
    void testSocialAuth_Success() {
        String loginType = "google";
        String expectedUrl = "https://accounts.google.com/o/oauth2/auth";

        when(authService.generateAuthUrl(loginType)).thenReturn(expectedUrl);

        ResponseEntity<String> response = userController.socialAuth(loginType, mock(HttpServletRequest.class));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrl, response.getBody());
    }




    @Test
    void deleteUser_Success() throws Exception {
        Long userId = 1L;

        doNothing().when(userService).deleteUser(userId);

        ResponseEntity<ResponseObject> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delete user successfully", response.getBody().getMessage());
        verify(userService, times(1)).deleteUser(userId);
    }


}