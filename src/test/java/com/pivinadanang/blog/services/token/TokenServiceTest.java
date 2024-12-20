// src/test/java/com/pivinadanang/blog/services/token/TokenServiceTest.java
package com.pivinadanang.blog.services.token;

import com.pivinadanang.blog.components.JwtTokenUtils;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.ExpiredTokenException;
import com.pivinadanang.blog.models.Token;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.repositories.TokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtTokenUtils jwtTokenUtil;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToken_WhenTokenCountLessThanMax() {
        UserEntity user = new UserEntity();
        String token = "testToken";
        boolean isMobileDevice = false;

        when(tokenRepository.findByUser(user)).thenReturn(Arrays.asList());

        Token newToken = tokenService.addToken(user, token, isMobileDevice);

        assertNotNull(newToken);
        assertEquals(token, newToken.getToken());
        verify(tokenRepository, times(1)).save(newToken);
    }

    @Test
    void testAddToken_WhenTokenCountExceedsMax() {
        UserEntity user = new UserEntity();
        String token = "testToken";
        boolean isMobileDevice = false;

        Token existingToken1 = new Token();
        existingToken1.setMobile(false);
        Token existingToken2 = new Token();
        existingToken2.setMobile(true);
        Token existingToken3 = new Token();
        existingToken3.setMobile(true);

        when(tokenRepository.findByUser(user)).thenReturn(Arrays.asList(existingToken1, existingToken2, existingToken3));

        Token newToken = tokenService.addToken(user, token, isMobileDevice);

        assertNotNull(newToken);
        assertEquals(token, newToken.getToken());
        verify(tokenRepository, times(1)).delete(existingToken1);
        verify(tokenRepository, times(1)).save(newToken);
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        UserEntity user = new UserEntity();
        String refreshToken = "testRefreshToken";
        Token existingToken = new Token();
        existingToken.setRefreshToken(refreshToken);
        existingToken.setRefreshExpirationDate(LocalDateTime.now().plusDays(1));

        when(tokenRepository.findByRefreshToken(refreshToken)).thenReturn(existingToken);
        when(jwtTokenUtil.generateToken(user)).thenReturn("newToken");

        Token refreshedToken = tokenService.refreshToken(refreshToken, user);

        assertNotNull(refreshedToken);
        assertEquals("newToken", refreshedToken.getToken());
        verify(tokenRepository, times(1)).save(existingToken);
    }

    @Test
    void testRefreshToken_NotFound() {
        UserEntity user = new UserEntity();
        String refreshToken = "testRefreshToken";

        when(tokenRepository.findByRefreshToken(refreshToken)).thenReturn(null);

        assertThrows(DataNotFoundException.class, () -> tokenService.refreshToken(refreshToken, user));
    }

    @Test
    void testRefreshToken_Expired() {
        UserEntity user = new UserEntity();
        String refreshToken = "testRefreshToken";
        Token existingToken = new Token();
        existingToken.setRefreshToken(refreshToken);
        existingToken.setRefreshExpirationDate(LocalDateTime.now().minusDays(1));

        when(tokenRepository.findByRefreshToken(refreshToken)).thenReturn(existingToken);

        assertThrows(ExpiredTokenException.class, () -> tokenService.refreshToken(refreshToken, user));
        verify(tokenRepository, times(1)).delete(existingToken);
    }
}