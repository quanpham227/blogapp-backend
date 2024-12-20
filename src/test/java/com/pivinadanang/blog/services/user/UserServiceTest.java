// src/test/java/com/pivinadanang/blog/services/user/UserServiceTest.java
package com.pivinadanang.blog.services.user;

import com.pivinadanang.blog.components.JwtTokenUtils;
import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.UpdateUserByAdminDTO;
import com.pivinadanang.blog.dtos.UpdateUserDTO;
import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.dtos.UserLoginDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.ExpiredTokenException;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.Token;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.repositories.RoleRepository;
import com.pivinadanang.blog.repositories.TokenRepository;
import com.pivinadanang.blog.repositories.UserRepository;
import com.pivinadanang.blog.responses.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtils jwtTokenUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private LocalizationUtils localizationUtils;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setFullName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setPhoneNumber("1234567890");
        userDTO.setPassword("password");
        userDTO.setRoleId(1L);

        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setName(RoleEntity.USER);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity());

        UserEntity createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void testGetUserDetailsFromToken_Success() throws Exception {
        String token = "testToken";
        String email = "test@example.com";
        UserEntity user = new UserEntity();
        user.setEmail(email);

        when(jwtTokenUtil.isTokenExpired(token)).thenReturn(false);
        when(jwtTokenUtil.getSubject(token)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserEntity result = userService.getUserDetailsFromToken(token);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testGetUserDetailsFromToken_ExpiredToken() {
        String token = "testToken";

        when(jwtTokenUtil.isTokenExpired(token)).thenReturn(true);

        assertThrows(ExpiredTokenException.class, () -> userService.getUserDetailsFromToken(token));
    }

    @Test
    void testGetUserDetailsFromRefreshToken_Success() throws Exception {
        String refreshToken = "testRefreshToken";
        UserEntity user = new UserEntity();
        Token token = new Token();
        token.setUser(user);
        token.setRefreshExpirationDate(LocalDateTime.now().plusDays(1));

        when(tokenRepository.findByRefreshToken(refreshToken)).thenReturn(token);

        UserEntity result = userService.getUserDetailsFromRefreshToken(refreshToken);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void testGetUserDetailsFromRefreshToken_ExpiredToken() {
        String refreshToken = "testRefreshToken";
        Token token = new Token();
        token.setRefreshExpirationDate(LocalDateTime.now().minusDays(1));

        when(tokenRepository.findByRefreshToken(refreshToken)).thenReturn(token);

        assertThrows(Exception.class, () -> userService.getUserDetailsFromRefreshToken(refreshToken));
        verify(tokenRepository, times(1)).delete(token);
    }

    @Test
    void testLogin_Success() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("test@example.com");
        userLoginDTO.setPassword("password");

        RoleEntity role = new RoleEntity();
        role.setName("USER");

        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setActive(true); // Ensure the user is active
        user.setRole(role); // Ensure the role is set

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtTokenUtil.generateToken(user)).thenReturn("jwtToken");

        String token = userService.login(userLoginDTO);

        assertNotNull(token);
        assertEquals("jwtToken", token);
    }
    @Test
    void testLogin_WrongPassword() {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("test@example.com");
        userLoginDTO.setPassword("wrongPassword");

        RoleEntity role = new RoleEntity();
        role.setName("USER");

        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setActive(true); // Ensure the user is active
        user.setRole(role); // Ensure the role is set

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.login(userLoginDTO));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setFullName("Updated User");
        updateUserDTO.setPhoneNumber("0987654321");

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setPhoneNumber("1234567890");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserEntity updatedUser = userService.updateUser(userId, updateUserDTO);

        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getFullName());
        assertEquals("0987654321", updatedUser.getPhoneNumber());
    }

    @Test
    void testUpdateUser_PhoneNumberAlreadyExists() {
        Long userId = 1L;
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setPhoneNumber("0987654321");

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setPhoneNumber("1234567890");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByPhoneNumber("0987654321")).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> userService.updateUser(userId, updateUserDTO));
    }

    @Test
    void testFindAll_Success() throws Exception {
        String keyword = "test";
        Boolean status = true;
        Long roleId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<UserEntity> users = Arrays.asList(new UserEntity(), new UserEntity());
        Page<UserEntity> userPage = new PageImpl<>(users);

        when(userRepository.findAll(keyword, status, roleId, pageable)).thenReturn(userPage);

        Page<UserEntity> result = userService.findAll(keyword, status, roleId, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testResetPassword_Success() throws Exception {
        Long userId = 1L;
        String newPassword = "newPassword";

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        userService.resetPassword(userId, newPassword);

        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void testBlockOrEnable_Success() throws DataNotFoundException {
        Long userId = 1L;
        Boolean active = true;

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.blockOrEnable(userId, active);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserByAdmin_Success() throws Exception {
        Long userId = 1L;
        UpdateUserByAdminDTO updateUserByAdminDTO = new UpdateUserByAdminDTO();
        updateUserByAdminDTO.setFullName("Admin Updated User");
        updateUserByAdminDTO.setPhoneNumber("0987654321");
        updateUserByAdminDTO.setRoleId(2L);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setPhoneNumber("1234567890");

        RoleEntity role = new RoleEntity();
        role.setId(2L);
        role.setName(RoleEntity.MODERATOR);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(role));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        UserResponse response = userService.updateUserByAdmin(userId, updateUserByAdminDTO);

        assertNotNull(response);
        assertEquals("Admin Updated User", response.getFullName());
        assertEquals("0987654321", response.getPhoneNumber());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(tokenRepository, times(1)).deleteByUserId(userId);
        verify(userRepository, times(1)).delete(user);
    }
}