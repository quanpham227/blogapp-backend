package com.pivinadanang.blog.filters;

import com.pivinadanang.blog.components.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.*;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtil;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if(isBypassToken(request)){
            filterChain.doFilter(request, response); //enable bypass
            return;
        }
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            final String token = authHeader.substring(7);
             final String phoneNumber = jwtTokenUtil.extractPhoneNumbers(token);
        }
    }
    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/posts**", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories**", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST")


//                // Healthcheck request, no JWT token required
//                Pair.of(String.format("%s/healthcheck/health", apiPrefix), "GET"),
//                Pair.of(String.format("%s/actuator/**", apiPrefix), "GET"),
//
//                Pair.of(String.format("%s/roles**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/policies**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/comments**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/coupons**", apiPrefix), "GET"),
//
//                Pair.of(String.format("%s/products**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/categories**", apiPrefix), "GET"),
//
//                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
//                Pair.of(String.format("%s/users/profile-images/**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/users/refreshToken", apiPrefix), "POST"),
//
//                // Swagger
//                Pair.of("/api-docs","GET"),
//                Pair.of("/api-docs/**","GET"),
//                Pair.of("/swagger-resources","GET"),
//                Pair.of("/swagger-resources/**","GET"),
//                Pair.of("/configuration/ui","GET"),
//                Pair.of("/configuration/security","GET"),
//                Pair.of("/swagger-ui/**","GET"),
//                Pair.of("/swagger-ui.html", "GET"),
//                Pair.of("/swagger-ui/index.html", "GET"),
//
//                //Đăng nhập social
//                Pair.of(String.format("%s/users/auth/social-login**", apiPrefix), "GET"),
//                Pair.of(String.format("%s/users/auth/social/callback**", apiPrefix), "GET")
        );
        for ( Pair<String, String> bypassToken : bypassTokens) {
            if(request.getServletPath().contains(bypassToken.getFirst()) &&
                    request.getMethod().equals(bypassToken.getSecond())) {
            }
        }
        return false;
    }
}
