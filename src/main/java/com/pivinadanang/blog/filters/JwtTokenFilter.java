package com.pivinadanang.blog.filters;

import com.pivinadanang.blog.components.JwtTokenUtils;
import com.pivinadanang.blog.models.UserEntity;
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
public class JwtTokenFilter extends OncePerRequestFilter{
    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtil;
    @Override
    protected void doFilterInternal(@NonNull  HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if(isBypassToken(request)) {
                filterChain.doFilter(request, response); //enable bypass
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "authHeader null or not started with Bearer");
                return;
            }
            final String token = authHeader.substring(7);
            final String email = jwtTokenUtil.extractEmails(token);
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserEntity userDetails = (UserEntity) userDetailsService.loadUserByUsername(email);
                if(jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
            filterChain.doFilter(request, response); //enable bypass
        }catch (Exception e) {
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }

    }


    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("/%s/healthcheck/health", apiPrefix), "GET"),
                Pair.of(String.format("/%s/actuator**", apiPrefix), "GET"),
                Pair.of(String.format("/%s/roles**", apiPrefix), "GET"),
                Pair.of(String.format("/%s/posts**", apiPrefix), "GET"),
                Pair.of(String.format("/%s/categories**", apiPrefix), "GET"),
                Pair.of(String.format("/%s/images**", apiPrefix), "GET"),
                Pair.of(String.format("/%s/slides**", apiPrefix), "GET"),
                Pair.of(String.format("/%s/clients**", apiPrefix), "GET"),
                Pair.of(String.format("/%s/users/forget-password", apiPrefix), "POST"),
                Pair.of(String.format("/%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("/%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("/%s/users/refreshToken", apiPrefix), "POST"),

                // Swagger
                // Swagger
                Pair.of("/api-docs","GET"),
                Pair.of("/api-docs/**","GET"),
                Pair.of("/swagger-resources","GET"),
                Pair.of("/swagger-resources/**","GET"),
                Pair.of("/configuration/ui","GET"),
                Pair.of("/configuration/security","GET"),
                Pair.of("/swagger-ui/**","GET"),
                Pair.of("/swagger-ui.html", "GET"),
                Pair.of("/swagger-ui/index.html", "GET")
        );

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();

        System.out.println("API Prefix: " + apiPrefix);
        System.out.println("Request Path: " + requestPath);
        System.out.println("Request Method: " + requestMethod);

        for (Pair<String, String> token : bypassTokens) {
            String path = token.getFirst();
            String method = token.getSecond();

            // Check if the request URI starts with the bypass path
            if (requestPath.matches(path.replace("**", ".*"))
                    && requestMethod.equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

}
