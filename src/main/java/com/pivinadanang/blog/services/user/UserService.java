package com.pivinadanang.blog.services.user;

import com.pivinadanang.blog.components.JwtTokenUtils;
import com.pivinadanang.blog.components.converters.LocalizationUtils;
import com.pivinadanang.blog.dtos.UpdateUserDTO;
import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.ExpiredTokenException;
import com.pivinadanang.blog.exceptions.PermissionDenyException;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.Token;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.repositories.RoleRepository;
import com.pivinadanang.blog.repositories.TokenRepository;
import com.pivinadanang.blog.repositories.UserRepository;
import com.pivinadanang.blog.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

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
        String email = jwtTokenUtil.extractEmails(token);
        Optional<UserEntity> user;
        user = userRepository.findByEmail(email);
        if (user.isPresent()) {
           return user.get();
        }else{
            throw new DataNotFoundException("User not found");
        }

    }

    @Override
    public UserEntity getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        // Kiểm tra nếu token không được tìm thấy
        if (existingToken == null) {
            throw new Exception("Refresh token không hợp lệ hoặc đã hết hạn.");
        }

        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(phoneNumber);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Invalid email or password");
        }
        UserEntity existingUser = optionalUser.get();

        //check password
        if ((existingUser.getFacebookAccountId() == null || existingUser.getFacebookAccountId().isEmpty()) &&
                (existingUser.getGoogleAccountId() == null || existingUser.getGoogleAccountId().isEmpty())) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Wrong email or password");
            }
        }
        Optional<RoleEntity> optionalRole = roleRepository.findById(roleId);
        if(optionalRole.isEmpty() || !roleId.equals(existingUser.getRole().getId())) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.ROLE_DOES_NOT_EXISTS));
        }
        if(!optionalUser.get().isActive()) {
            throw new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_LOCKED));
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
              phoneNumber, password,
                existingUser.getAuthorities()
        );
        //authenticate with Java Spring security
        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }
    @Transactional
    @Override
    public UserEntity updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        // Find the existing user by userId
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if the phone number is being changed and if it already exists for another user
        String phoneNumber = updatedUserDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(phoneNumber) &&
                userRepository.existsByPhoneNumber(phoneNumber)) {
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
}
