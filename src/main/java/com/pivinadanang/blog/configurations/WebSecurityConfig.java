package com.pivinadanang.blog.configurations;

import com.pivinadanang.blog.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix)
                            ).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/posts", apiPrefix),
                                    String.format("%s/posts/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/categories", apiPrefix),
                                    String.format("%s/categories/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/posts/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PATCH,
                                    String.format("%s/posts/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(DELETE,
                                    String.format("%s/posts/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST,
                                    String.format("%s/categories/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST,
                                    String.format("%s/categories", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT,
                                    String.format("%s/categories/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(DELETE,
                                    String.format("%s/categories/**", apiPrefix)).hasRole("ADMIN")
                            .anyRequest().authenticated();
                });
        return http.build();
    }

}
