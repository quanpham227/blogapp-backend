package com.pivinadanang.blog.services.user;

import com.pivinadanang.blog.components.JwtTokenUtils;
import com.pivinadanang.blog.components.LocalizationUtils;
import com.pivinadanang.blog.dtos.UpdateUserByAdminDTO;
import com.pivinadanang.blog.dtos.UpdateUserDTO;
import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.dtos.UserLoginDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.ExpiredTokenException;
import com.pivinadanang.blog.exceptions.InvalidPasswordException;
import com.pivinadanang.blog.exceptions.PermissionDenyException;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.Token;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.repositories.RoleRepository;
import com.pivinadanang.blog.repositories.TokenRepository;
import com.pivinadanang.blog.repositories.UserRepository;
import com.pivinadanang.blog.responses.user.UserResponse;
import com.pivinadanang.blog.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.pivinadanang.blog.ultils.ValidationUtils.isValidPhoneNumber;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final LocalizationUtils localizationUtils;
    @Override
    public UserEntity createUser(UserDTO userDTO) throws Exception {
        if (!userDTO.getPhoneNumber().isBlank() && userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        if (!userDTO.getEmail().isBlank() && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        RoleEntity role =roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));


        if (role.getName().equalsIgnoreCase(RoleEntity.ADMIN)) {
            throw new PermissionDenyException("Registering admin accounts is not allowed");
        }
        //convert from userDTO => user
        UserEntity newUser = UserEntity.builder()
                .fullName(userDTO.getFullName())
                .email(userDTO.getEmail())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(userDTO.getPassword())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .active(true)
                .build();

        newUser.setRole(role);

        if (!userDTO.isSocialLogin()) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }
        return userRepository.save(newUser);

    }

    @Override
    public UserEntity getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubject(token);
        Optional<UserEntity> user;
        user = userRepository.findByEmail(subject);
        if (user.isEmpty() && isValidPhoneNumber(subject)) {
            user = userRepository.findByPhoneNumber(subject);
        }
        return user.orElseThrow(() -> new Exception("User not found"));

    }



    @Override
    public UserEntity getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        // Kiểm tra nếu token không được tìm thấy
        if (existingToken == null) {
            throw new Exception("Refresh token không hợp lệ hoặc đã hết hạn.");
        }
        // Kiểm tra thời gian hết hạn của refresh token
        if (existingToken.getRefreshExpirationDate().isBefore(LocalDateTime.now())) {
            // Xóa refresh token hết hạn
            tokenRepository.delete(existingToken);
            throw new Exception("Refresh token đã hết hạn.");
        }
        // Lấy thông tin người dùng từ token
        UserEntity user = existingToken.getUser();

        // Kiểm tra xem người dùng có tồn tại không
        if (user == null) {
            throw new Exception("Không tìm thấy người dùng.");
        }
        return user;

    }

    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        Optional<UserEntity> optionalUser = Optional.empty();
        String subject = null;

        // Check if the user exists by email
        if (userLoginDTO.getEmail() != null && !userLoginDTO.getEmail().isBlank()) {
            optionalUser = userRepository.findByEmail(userLoginDTO.getEmail());
            subject = userLoginDTO.getEmail();
        }

        // If the user is not found by email, check by phone number
        if (optionalUser.isEmpty() && userLoginDTO.getPhoneNumber() != null) {
            optionalUser = userRepository.findByPhoneNumber(userLoginDTO.getPhoneNumber());
            subject = userLoginDTO.getPhoneNumber();
        }

        // If user is not found, throw an exception
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.WRONG_PHONE_PASSWORD));
        }

        // Get the existing user
        UserEntity existingUser = optionalUser.get();

        // Check if the user is active
        if (!existingUser.isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }

        // Check if the account is linked to Facebook or Google
        if (existingUser.getFacebookAccountId() != null && !existingUser.getFacebookAccountId().isEmpty()) {
            throw new BadCredentialsException("Tài khoản này được đăng ký bằng Facebook. Vui lòng đăng nhập bằng Facebook.");
        }
        if (existingUser.getGoogleAccountId() != null && !existingUser.getGoogleAccountId().isEmpty()) {
            throw new BadCredentialsException("Tài khoản này được đăng ký bằng Google. Vui lòng đăng nhập bằng Google.");
        }

        // Check password
        if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
            throw new BadCredentialsException("Wrong email or password");
        }

        // Authenticate with Java Spring Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                subject, userLoginDTO.getPassword(), existingUser.getAuthorities());
        authenticationManager.authenticate(authenticationToken);

        // Generate JWT token
        return jwtTokenUtil.generateToken(existingUser);
    }

    @Transactional
    @Override
    public UserEntity updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Find the existing user by userId
        UserEntity existingUser = getUserById(userId);

        // Check if the phone number is being changed and if it already exists for another user
        String phoneNumber = updatedUserDTO.getPhoneNumber();
        if ((existingUser.getPhoneNumber() == null || !existingUser.getPhoneNumber().equals(phoneNumber)) &&
                phoneNumber != null && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }


        // Update user information based on the DTO
        if (updatedUserDTO.getFullName() != null) {
            existingUser.setFullName(updatedUserDTO.getFullName());
        }
        if (phoneNumber != null) {
            existingUser.setPhoneNumber(phoneNumber);
        }

        if (updatedUserDTO.getFacebookAccountId() != null) {
            existingUser.setFacebookAccountId(updatedUserDTO.getFacebookAccountId());
        }
        if (updatedUserDTO.getGoogleAccountId() != null) {
            existingUser.setGoogleAccountId(updatedUserDTO.getGoogleAccountId());
        }

        // Update the password if it is provided in the DTO
        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()) {
            if(!updatedUserDTO.getPassword().equals(updatedUserDTO.getRetypePassword())){
                throw new DataIntegrityViolationException("Password not match");
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }
        //existingUser.setRole(updatedRole);
        // Save the updated user
        return userRepository.save(existingUser);
    }

    @Override
    public Page<UserEntity> findAll(String keyword, Boolean status, Long roleId, Pageable pageable) throws Exception {
        Page<UserEntity> userPage = userRepository.findAll(keyword, status, roleId, pageable);
        return userPage != null ? userPage : Page.empty(pageable); // Đảm bảo trả về `Page.empty` nếu kết quả là `null`.
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) throws InvalidPasswordException, DataNotFoundException{
        UserEntity existingUser = getUserById(userId);
        String encodedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedPassword);
        userRepository.save(existingUser);
        //reset password => clear token
        List<Token> tokens = tokenRepository.findByUser(existingUser);
        tokenRepository.deleteAll(tokens);
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }



    @Override
    @Transactional
    public UserResponse updateUserByAdmin(Long userId, UpdateUserByAdminDTO updateUserByAdminDTO) throws Exception {
        UserEntity existingUser = getUserById(userId);

        // Cập nhật các thuộc tính
        if (updateUserByAdminDTO.getFullName() != null) {
            existingUser.setFullName(updateUserByAdminDTO.getFullName());
        }
        if (updateUserByAdminDTO.getPhoneNumber() != null) {
            if (!existingUser.getPhoneNumber().equals(updateUserByAdminDTO.getPhoneNumber()) &&
                    userRepository.existsByPhoneNumber(updateUserByAdminDTO.getPhoneNumber())) {
                throw new DataIntegrityViolationException("Phone number already exists");
            }
            existingUser.setPhoneNumber(updateUserByAdminDTO.getPhoneNumber());
        }
        if (updateUserByAdminDTO.getFacebookAccountId() != null) {
            existingUser.setFacebookAccountId(updateUserByAdminDTO.getFacebookAccountId());
        }
        if (updateUserByAdminDTO.getGoogleAccountId() != null) {
            existingUser.setGoogleAccountId(updateUserByAdminDTO.getGoogleAccountId());
        }
        if (updateUserByAdminDTO.getPassword() != null && !updateUserByAdminDTO.getPassword().isEmpty()) {
            if (!updateUserByAdminDTO.getPassword().equals(updateUserByAdminDTO.getRetypePassword())) {
                throw new DataIntegrityViolationException("Password not match");
            }
            existingUser.setPassword(passwordEncoder.encode(updateUserByAdminDTO.getPassword()));
        }

        // Cập nhật role (Moderator mới có quyền, đã kiểm tra ở Controller)
        if (updateUserByAdminDTO.getRoleId() != null) {
            existingUser.setRole(roleRepository.findById(updateUserByAdminDTO.getRoleId())
                    .orElseThrow(() -> new DataNotFoundException(
                            localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS))));
        }

        UserEntity savedUser = userRepository.save(existingUser);
        return UserResponse.fromUser(savedUser);
    }


    @Override
    public UserEntity getUserById(Long userId) throws DataNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
    }

    @Override
    public String loginSocial(UserLoginDTO userLoginDTO) throws Exception {
        Optional<UserEntity> optionalUser = Optional.empty();
        RoleEntity roleUser = roleRepository.findByName(RoleEntity.USER)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS)));

        // Kiểm tra Google Account ID
        if (userLoginDTO.isGoogleAccountIdValid()) {
            optionalUser = userRepository.findByGoogleAccountId(userLoginDTO.getGoogleAccountId());

            // Tạo người dùng mới nếu không tìm thấy
            if (optionalUser.isEmpty()) {
                UserEntity newUser = UserEntity.builder()
                        .fullName(Optional.ofNullable(userLoginDTO.getFullName()).orElse(""))
                        .email(Optional.ofNullable(userLoginDTO.getEmail()).orElse(""))
                        .profileImage(Optional.ofNullable(userLoginDTO.getProfileImage()).orElse(""))
                        .role(roleUser)
                        .googleAccountId(userLoginDTO.getGoogleAccountId())
                        .password("") // Mật khẩu trống cho đăng nhập mạng xã hội
                        .active(true)
                        .build();

                // Lưu người dùng mới
                newUser = userRepository.save(newUser);
                optionalUser = Optional.of(newUser);
            }
        }
        // Kiểm tra Facebook Account ID
        else if (userLoginDTO.isFacebookAccountIdValid()) {
            optionalUser = userRepository.findByFacebookAccountId(userLoginDTO.getFacebookAccountId());

            // Tạo người dùng mới nếu không tìm thấy
            if (optionalUser.isEmpty()) {
                UserEntity newUser = UserEntity.builder()
                        .fullName(Optional.ofNullable(userLoginDTO.getFullName()).orElse(""))
                        .email(Optional.ofNullable(userLoginDTO.getEmail()).orElse(""))
                        .profileImage(Optional.ofNullable(userLoginDTO.getProfileImage()).orElse(""))
                        .role(roleUser)
                        .facebookAccountId(userLoginDTO.getFacebookAccountId())
                        .password("") // Mật khẩu trống cho đăng nhập mạng xã hội
                        .active(true)
                        .build();

                // Lưu người dùng mới
                newUser = userRepository.save(newUser);
                optionalUser = Optional.of(newUser);
            }
        } else {
            throw new IllegalArgumentException("Invalid social account information.");
        }

        UserEntity user = optionalUser.get();

        // Kiểm tra nếu tài khoản bị khóa
        if (!user.isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }

        // Tạo JWT token cho người dùng
        return jwtTokenUtil.generateToken(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) throws Exception {
        // Xóa các token liên quan đến người dùng
        tokenRepository.deleteByUserId(userId);

        // Xóa người dùng
        UserEntity existingUser = getUserById(userId);
        userRepository.delete(existingUser);
    }

}
