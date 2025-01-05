package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.*;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.InvalidPasswordException;
import com.pivinadanang.blog.exceptions.PermissionDenyException;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.Token;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.user.LoginResponse;
import com.pivinadanang.blog.responses.user.UserListResponse;
import com.pivinadanang.blog.responses.user.UserResponse;
import com.pivinadanang.blog.services.auth.IAuthService;
import com.pivinadanang.blog.services.role.RoleService;
import com.pivinadanang.blog.services.token.ITokenService;
import com.pivinadanang.blog.services.user.IUserService;
import com.pivinadanang.blog.ultils.MessageKeys;
import com.pivinadanang.blog.ultils.ValidationUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;
    private final ITokenService tokenService;
    private final RoleService roleService;
    private final IAuthService authService;


    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getAllUser(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "0", name = "roleId") Long roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws Exception {
        Logger logger = LoggerFactory.getLogger(UserController.class);
        logger.debug("Entering getAllUser with params: keyword={}, status={}, roleId={}, page={}, limit={}",
                keyword, status, roleId, page, limit);
        if (page < 0) {
            return ResponseEntity.badRequest()
                    .body(ResponseObject.builder()
                            .message("Page index must not be less than zero")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        try {
            Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").ascending());
            Page<UserResponse> userPage = userService.findAll(keyword, status, roleId, pageable)
                    .map(UserResponse::fromUser);
            int totalPages = userPage.getTotalPages();
            List<UserResponse> userResponses = userPage.getContent();
            UserListResponse userListResponse = UserListResponse
                    .builder()
                    .users(userResponses)
                    .totalPages(totalPages)
                    .build();
            return buildOkResponse(userListResponse, "Get users successfully");
        } catch (DataNotFoundException e) {
            logger.error("DataNotFoundException in getAllUser: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseObject.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.NOT_FOUND)
                            .build());
        } catch (RuntimeException exception) {
            logger.error("Exception in getAllUser: ", exception);
            return buildErrorResponse(exception);
        }
    }
    @PostMapping("/register")
    public ResponseEntity<ResponseObject> createUser(@Valid @RequestBody UserDTO userDTO,
                                                     BindingResult result) throws Exception {

        if (result.hasErrors()) {
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
                // phone number not blank
                if (!ValidationUtils.isValidPhoneNumber(userDTO.getPhoneNumber())) {
                    return ResponseEntity.badRequest().body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .message("Invalid phone number")
                            .build());
                }
            }
        } else {
            // Email not blank
            if (!ValidationUtils.isValidEmail(userDTO.getEmail())) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .data(null)
                        .message("Invalid email format")
                        .build());
            }
        }

        // Check if password and retype password match
        if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                    .build());
        }

        UserEntity user = userService.createUser(userDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(UserResponse.fromUser(user))
                .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                .build());
    }
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login(@Valid @RequestBody UserLoginDTO userLoginDTO,
                                                HttpServletRequest request,
                                                HttpServletResponse response) throws Exception {
        // Kiểm tra thông tin đăng nhập và sinh token
        String token = userService.login(userLoginDTO);
        // Lấy thông tin user từ token
        String userAgent = request.getHeader("User-Agent");
        UserEntity userDetail = userService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        // Thiết lập cookies từ phía server
        setCookies(response, jwtToken.getRefreshToken());

        LoginResponse loginResponse = LoginResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(token)
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId())
                .build();

        // Trả về token trong response
        return ResponseEntity.ok(ResponseObject.builder()
                .message("Login successfully")
                .data(loginResponse)
                .status(HttpStatus.OK)
                .build());
    }
    @PostMapping("/refreshToken")
    public ResponseEntity<ResponseObject> refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("You are not logged in, please login again")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String extractedToken = authorizationHeader.substring(7); // Remove "Bearer " from the token string
        UserEntity user = userService.getUserDetailsFromToken(extractedToken);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get user details successfully")
                .data(UserResponse.fromUser(user))
                .status(HttpStatus.OK)
                .build());
    }

    @PutMapping("/details/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractedToken = authorizationHeader.substring(7);
        UserEntity user = userService.getUserDetailsFromToken(extractedToken);
        // Ensure that the user making the request matches the user being updated
        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        UserEntity updatedUser = userService.updateUser(userId, updatedUserDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Update user details successfully")
                .data(UserResponse.fromUser(updatedUser))
                .status(HttpStatus.OK)
                .build());
    }
    @PutMapping("/admin/update/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateUserByAdmin(
            @PathVariable Long userId,
            @RequestBody UpdateUserByAdminDTO updateUserByAdminDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractedToken = authorizationHeader.substring(7);
        UserEntity requester = userService.getUserDetailsFromToken(extractedToken);
        UserEntity targetUser = userService.getUserById(userId);

        // Moderator có toàn quyền
        if (requester.getRole().getName().equals(RoleEntity.MODERATOR)) {
            // Không cần kiểm tra gì thêm
        } else if (requester.getRole().getName().equals(RoleEntity.ADMIN)) {
            // Kiểm tra nếu Admin cập nhật chính tài khoản của họ
            if (requester.getId().equals(userId)) {
                // Admin chỉ không được thay đổi role thành Moderator
                if (updateUserByAdminDTO.getRoleId() != null) {
                    RoleEntity requestedRole = roleService.getRoleById(updateUserByAdminDTO.getRoleId());
                    if (requestedRole.getName().equals(RoleEntity.MODERATOR)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ResponseObject.builder()
                                        .message("Admins cannot change their role to Moderator")
                                        .status(HttpStatus.BAD_REQUEST)
                                        .build());
                    }
                }
            } else {
                // Không được cập nhật thông tin của Admin khác hoặc Moderator
                if (targetUser.getRole().getName().equals(RoleEntity.ADMIN)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseObject.builder()
                                    .message("Admins cannot update other Admins")
                                    .status(HttpStatus.BAD_REQUEST)
                                    .build());
                }
                if (targetUser.getRole().getName().equals(RoleEntity.MODERATOR)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseObject.builder()
                                    .message("Admins cannot update Moderators")
                                    .status(HttpStatus.BAD_REQUEST)
                                    .build());
                }

                // Admin không được thay đổi role của bất kỳ ai
                if (updateUserByAdminDTO.getRoleId() != null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ResponseObject.builder()
                                    .message("Admins cannot change roles")
                                    .status(HttpStatus.BAD_REQUEST)
                                    .build());
                }
            }
        } else {
            // User không có quyền
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ResponseObject.builder()
                            .message("Access denied")
                            .status(HttpStatus.FORBIDDEN)
                            .build());
        }

        // Thực hiện cập nhật
        UserResponse updatedUser = userService.updateUserByAdmin(userId, updateUserByAdminDTO);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Update user successfully")
                .data(updatedUser)
                .status(HttpStatus.OK)
                .build());
    }



    @PutMapping("/reset-password/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> resetPassword(@Valid @PathVariable long userId) {
        try {
            String newPassword = UUID.randomUUID().toString().substring(0, 5); // Generate new password
            userService.resetPassword(userId, newPassword);
            return ResponseEntity.ok(ResponseObject.builder()
                    .message("Reset password successfully")
                    .data(newPassword)
                    .status(HttpStatus.OK)
                    .build());
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                    .message("Invalid password")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseObject.builder()
                    .message("User not found")
                    .data("")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseObject.builder()
                    .message("Data integrity violation")
                    .data("")
                    .status(HttpStatus.CONFLICT)
                    .build());
        } catch (PermissionDenyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseObject.builder()
                    .message("Permission denied")
                    .data("")
                    .status(HttpStatus.FORBIDDEN)
                    .build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseObject.builder()
                    .message(e.getMessage())
                    .data("")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build());
        }
    }
// src/main/java/com/pivinadanang/blog/controller/UserController.java

    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> blockOrEnable(
            @Valid @PathVariable long userId,
            @Valid @PathVariable int active,
            @RequestHeader("Authorization") String authorizationHeader
    ) throws Exception {
        String extractedToken = authorizationHeader.substring(7);
        UserEntity requester = userService.getUserDetailsFromToken(extractedToken);
        UserEntity targetUser = userService.getUserById(userId);

        // Moderator có quyền block/unblock tất cả tài khoản
        if (requester.getRole().getName().equals(RoleEntity.MODERATOR)) {
            userService.blockOrEnable(userId, active > 0);
        } else if (requester.getRole().getName().equals(RoleEntity.ADMIN)) {
            // Admin chỉ có quyền block/unblock User
            if (targetUser.getRole().getName().equals(RoleEntity.USER)) {
                userService.blockOrEnable(userId, active > 0);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ResponseObject.builder()
                                .message("Access denied")
                                .status(HttpStatus.BAD_REQUEST)
                                .build());
            }
        } else {
            // Nếu là User hoặc vai trò khác
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.builder()
                            .message("Access denied")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }

        String message = active > 0 ? "Successfully enabled the user." : "Successfully blocked the user.";
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }

    //@PostMapping("/login/social")
    private ResponseEntity<ResponseObject> loginSocial(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        // Gọi hàm loginSocial từ UserService cho đăng nhập mạng xã hội
        String token = userService.loginSocial(userLoginDTO);

        // Xử lý token và thông tin người dùng
        String userAgent = request.getHeader("User-Agent");
        UserEntity userDetail = userService.getUserDetailsFromToken(token);
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        // Thiết lập cookies từ phía server
        setCookies(response, jwtToken.getRefreshToken());

        // Tạo đối tượng LoginResponse
        LoginResponse loginResponse = LoginResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(jwtToken.getToken())
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId())
                .build();

        // Trả về phản hồi
        return ResponseEntity.ok().body(
                ResponseObject.builder()
                        .message("Login successfully")
                        .data(loginResponse)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    //Angular, bấm đăng nhập gg, redirect đến trang đăng nhập google, đăng nhập xong có "code"
    //Từ "code" => google token => lấy ra các thông tin khác
    @GetMapping("/auth/social-login")
    public ResponseEntity<String> socialAuth(@RequestParam("login_type") String loginType, HttpServletRequest request){
        //request.getRequestURI()
        loginType = loginType.trim().toLowerCase();  // Loại bỏ dấu cách và chuyển thành chữ thường
        String url = authService.generateAuthUrl(loginType);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/auth/social/callback")
    public ResponseEntity<ResponseObject> callback(@RequestParam("code") String code, @RequestParam("login_type") String loginType, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Call the AuthService to get user info
        Map<String, Object> userInfo = authService.authenticateAndFetchProfile(code, loginType);

        if (userInfo == null) {
            return ResponseEntity.badRequest().body(new ResponseObject(
                    "Failed to authenticate", HttpStatus.BAD_REQUEST, null
            ));
        }

        // Extract user information from userInfo map
        String accountId = "";
        String name = "";
        String picture = "";
        String email = "";

        if (loginType.trim().equals("google")) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("sub"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            picture = (String) Objects.requireNonNullElse(userInfo.get("picture"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
        } else if (loginType.trim().equals("facebook")) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("id"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
            // Lấy URL ảnh từ cấu trúc dữ liệu của Facebook
            Object pictureObj = userInfo.get("picture");
            if (pictureObj instanceof Map) {
                Map<?, ?> pictureData = (Map<?, ?>) pictureObj;
                Object dataObj = pictureData.get("data");
                if (dataObj instanceof Map) {
                    Map<?, ?> dataMap = (Map<?, ?>) dataObj;
                    Object urlObj = dataMap.get("url");
                    if (urlObj instanceof String) {
                        picture = (String) urlObj;
                    }
                }
            }
        }

        // Tạo đối tượng UserLoginDTO
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .email(email)
                .fullName(name)
                .password("")
                .phoneNumber("")
                .profileImage(picture)
                .build();

        if (loginType.trim().equals("google")) {
            userLoginDTO.setGoogleAccountId(accountId);
            //userLoginDTO.setFacebookAccountId("");
        } else if (loginType.trim().equals("facebook")) {
            userLoginDTO.setFacebookAccountId(accountId);
            //userLoginDTO.setGoogleAccountId("");
        }

        return this.loginSocial(userLoginDTO, request, response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteUser(@PathVariable Long id) throws Exception {
        userService.deleteUser(id);
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Delete user successfully")
                .status(HttpStatus.OK)
                .data(null)
                .build());
    }


    private ResponseEntity<ResponseObject> buildErrorResponse(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseObject.builder()
                        .message(e.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build());
    }
    private ResponseEntity<ResponseObject> buildOkResponse(Object data, String message) {
        return ResponseEntity.ok(ResponseObject.builder()
                .message(message)  // Truyền trực tiếp thông điệp
                .status(HttpStatus.OK)
                .data(data)
                .build());
    }
}
