package com.pivinadanang.blog.controller;

import com.pivinadanang.blog.components.converters.LocalizationUtils;
import com.pivinadanang.blog.dtos.UpdateUserDTO;
import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.dtos.UserLoginDTO;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.responses.user.LoginResponse;
import com.pivinadanang.blog.responses.user.UserResponse;
import com.pivinadanang.blog.services.user.IUserService;
import com.pivinadanang.blog.ultils.MessageKeys;
import com.pivinadanang.blog.ultils.ValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.GrantedAuthority;


import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;

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

        // Kiểm tra xem email có trống hoặc chỉ chứa khoảng trắng không
        if ((userDTO.getEmail()== null || userDTO.getEmail().isBlank())) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("Email  is required")
                    .build());
        }
        // Kiểm tra xem số điện thoại có trống hoặc chỉ chứa khoảng trắng không
        if ((userDTO.getPhoneNumber() == null || userDTO.getPhoneNumber().trim().isBlank())) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("Phone  number is required")
                    .build());
        }

        // Kiểm tra định dạng email
        if (!ValidationUtils.isValidEmail(userDTO.getEmail())) {
            throw new Exception("Invalid email");
        }

        // Kiểm tra định dạng số điện thoại
        if (!ValidationUtils.isValidPhoneNumber(userDTO.getPhoneNumber())) {
            throw new Exception("Invalid email format");
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
                            .status(HttpStatus.CREATED)
                            .data(UserResponse.fromUser(user))
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESSFULLY))
                            .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> login (@Valid @RequestBody UserLoginDTO userLoginDTO
                                                ) throws Exception{
        String token = userService.login(
                userLoginDTO.getEmail(),
                userLoginDTO.getPassword(),
                userLoginDTO.getRoleId() == null ? 2 : userLoginDTO.getRoleId());
        UserEntity userDetail = userService.getUserDetailsFromToken(token);
        LoginResponse loginResponse = LoginResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                .token(token)
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()) //method reference
                .id(userDetail.getId())
                .build();
        return  ResponseEntity.ok(ResponseObject.builder()
                    .message("Login successfully")
                    .data(loginResponse)
                    .status(HttpStatus.OK)
                .build());
      }

    @PostMapping("/details")
    //@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<UserResponse> getUserDetails(@RequestHeader("Authorization") String auhorizationHeader) {
        try {
            String extractedToken = auhorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            UserEntity user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/details/{userId}")
    //@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<UserResponse> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO updatedUserDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String extractedToken = authorizationHeader.substring(7);
            UserEntity user = userService.getUserDetailsFromToken(extractedToken);
            // Ensure that the user making the request matches the user being updated
            if (user.getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            UserEntity updatedUser = userService.updateUser(userId, updatedUserDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
