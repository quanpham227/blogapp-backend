package com.pivinadanang.blog.components;
import com.pivinadanang.blog.models.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public UserEntity getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null &&
                authentication.getPrincipal() instanceof UserEntity selectedUser) {
            if(!selectedUser.isActive()) {
                return null;
            }
            return (UserEntity) authentication.getPrincipal();
        }
        return null;
    }
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role));
    }
}
