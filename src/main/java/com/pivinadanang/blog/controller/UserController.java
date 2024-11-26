package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.RefreshTokenDTO;
import com.pivinadanang.blog.dtos.UpdateUserDTO;
import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.dtos.UserLoginDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.InvalidPasswordException;
import com.pivinadanang.blog.models.Token;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.user.LoginResponse;
import com.pivinadanang.blog.responses.user.UserListResponse;
import com.pivinadanang.blog.responses.user.UserResponse;
import com.pivinadanang.blog.services.token.ITokenService;
import com.pivinadanang.blog.services.user.IUserService;
import com.pivinadanang.blog.ultils.MessageKeys;
import com.pivinadanang.blog.ultils.ValidationUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;
    private final ITokenService tokenService;


    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> getAllUser(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws Exception{
        // Tạo Pageable từ thông tin trang và giới hạn
        Pageable pageable = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("create").ascending()
        );
        Page<UserResponse> userPage = userService.findAll(keyword, pageable)
                .map(UserResponse::fromUser);

        // Lấy tổng số trang
        int totalPages = userPage.getTotalPages();
        List<UserResponse> userResponses = userPage.getContent();
        UserListResponse userListResponse = UserListResponse
                .builder()
                .users(userResponses)
                .totalPages(totalPages)
                .build();
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get user list successfully")
                .status(HttpStatus.OK)
                .data(userListResponse)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> createUser(@Valid @RequestBody UserDTO userDTO,
                                                     BindingResult result) throws Exception{
            if(result.hasErrors()){
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .message(errorMessages.toString())
                    .build());

        }


        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isBlank()) {
            if (userDTO.getPhoneNumber() == null || userDTO.getPhoneNumber().isBlank()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .data(null)
                        .message("At least email or phone number is required")
                        .build());
            } else {
                //phone number not blank
                if (!ValidationUtils.isValidPhoneNumber(userDTO.getPhoneNumber())) {
                    throw new Exception("Invalid phone number");
                }
            }
        } else {
            //Email not blank
            if (!ValidationUtils.isValidEmail(userDTO.getEmail())) {
                throw new Exception("Invalid email format");
            }
        }
        // Kiểm tra mật khẩu và xác nhận mật khẩu có trùng khớp không
        if(!userDTO.getPassword().equals(userDTO.getRetypePassword())){
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    . status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build());
        }

        UserEntity user = userService.createUser(userDTO);
            return ResponseEntity.ok(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .data(UserResponse.fromUser(user))
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                            .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login (@Valid @RequestBody UserLoginDTO userLoginDTO,
                                                 HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception{
        // Kiểm tra thông tin đăng nhập và sinh token
            String token = userService.login(userLoginDTO);
            // Lấy thông tin user từ token
            String userAgent = request.getHeader("User-Agent");
            UserEntity userDetail = userService.getUserDetailsFromToken(token);
            Token jwtToken =  tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

            // Thiết lập cookies từ phía server

        // Thiết lập cookies từ phía server
        setCookies(response, jwtToken.getRefreshToken());

            LoginResponse loginResponse = LoginResponse.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                    .token(token)
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) //method reference
                    .id(userDetail.getId())
                    .build();

            // Trả về token trong response
            return  ResponseEntity.ok(ResponseObject.builder()
                    .message("Login successfully")
                    .data(loginResponse)
                    .status(HttpStatus.OK)
                    .build());

      }
    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseObject> refreshToken( HttpServletRequest request, HttpServletResponse response) throws Exception {String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("You not login, please login again")
                    .build());
        }
        UserEntity userDetail = userService.getUserDetailsFromRefreshToken(refreshToken);
        Token jwtToken = tokenService.refreshToken(refreshToken, userDetail);

        // Thiết lập cookies từ phía server
        setCookies(response, jwtToken.getRefreshToken());


        LoginResponse loginResponse = LoginResponse.builder()
                .message("Refresh token successfully")
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId()).build();
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .data(loginResponse)
                        .message(loginResponse.getMessage())
                        .status(HttpStatus.OK)
                        .build());

    }
    private void setCookies(HttpServletResponse response, String refreshToken) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true) // Đặt httpOnly là true để tăng cường bảo mật
                .secure(false) // Đặt secure là true nếu bạn sử dụng HTTPS , đang ở gd pt thì để false đã
                .path("/")
                .sameSite("Lax")
                .maxAge(30 * 24 * 60 * 60) // 30 ngày
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseObject> logout(HttpServletResponse response) {
        // Xóa cookie refreshToken
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // Đặt secure là true nếu bạn sử dụng HTTPS
                .path("/")
                .sameSite("Lax")
                .maxAge(0) // Đặt maxAge là 0 để xóa cookie
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(ResponseObject.builder()
                .message("Logged out successfully")
                .status(HttpStatus.OK)
                .build());
    }
    private boolean isMobileDevice(String userAgent) {
        // Kiểm tra User-Agent header để xác định thiết bị di động
        // Ví dụ đơn giản:
        return userAgent.toLowerCase().contains("mobile");
    }

    @PostMapping("/details")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getUserDetails(@RequestHeader("Authorization") String auhorizationHeader) throws Exception {

            String extractedToken = auhorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            UserEntity user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Get user details successfully")
                    .data(UserResponse.fromUser(user))
                    .status(HttpStatus.OK)
                    .build());

    }

    @PutMapping("/details/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractedToken = authorizationHeader.substring(7);
        UserEntity user = userService.getUserDetailsFromToken(extractedToken);
        // Ensure that the user making the request matches the user being updated
        if (user.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserEntity updatedUser = userService.updateUser(userId, updatedUserDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Update user details successfully")
                .data(UserResponse.fromUser(updatedUser))
                .status(HttpStatus.OK)
                .build());
    }
    @PutMapping("/reset-password/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> resetPassword(@Valid @PathVariable long userId) {
        try {
            String newPassword = UUID.randomUUID().toString().substring(0, 5); // Tạo mật khẩu mới
            userService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Reset password successfully")
                    .data(newPassword)
                    .status(HttpStatus.OK)
                    .build());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Invalid password")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("User not found")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> blockOrEnable(
            @Valid @PathVariable long userId,
            @Valid @PathVariable int active
    ) throws Exception {
        userService.blockOrEnable(userId, active > 0);
        String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }

}
