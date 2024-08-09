package com.pivinadanang.blog.services.user;

import com.pivinadanang.blog.components.JwtTokenUtils;
import com.pivinadanang.blog.components.converters.LocalizationUtils;
import com.pivinadanang.blog.dtos.UserDTO;
import com.pivinadanang.blog.exceptions.DataNotFoundException;
import com.pivinadanang.blog.exceptions.ExpiredTokenException;
import com.pivinadanang.blog.exceptions.PermissionDenyException;
import com.pivinadanang.blog.models.RoleEntity;
import com.pivinadanang.blog.models.UserEntity;
import com.pivinadanang.blog.repositories.RoleRepository;
import com.pivinadanang.blog.repositories.UserRepository;
import com.pivinadanang.blog.ultils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.pivinadanang.blog.ultils.ValidationUtils.isValidEmail;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
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
        user = userRepository.findByPhoneNumber(subject);
        if (user.isEmpty() && isValidEmail(subject)) {
            user = userRepository.findByEmail(subject);
        }
        return user.orElseThrow(() -> new Exception("User not found"));
    }

    @Override
    public String login(String phoneNumber, String password, Long roleId) throws Exception {
        Optional<UserEntity> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if(optionalUser.isEmpty()){
            throw new DataNotFoundException("Invalid phone number or password");
        }
        UserEntity existingUser = optionalUser.get();

        //check password
        if ((existingUser.getFacebookAccountId() == null || existingUser.getFacebookAccountId().isEmpty()) &&
                (existingUser.getGoogleAccountId() == null || existingUser.getGoogleAccountId().isEmpty())) {
            if (!passwordEncoder.matches(password, existingUser.getPassword())) {
                throw new BadCredentialsException("Wrong phone number or password");
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
}
