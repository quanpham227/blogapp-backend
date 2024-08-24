package com.pivinadanang.blog.configurations;

import com.pivinadanang.blog.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity (debug = true)
@EnableMethodSecurity(prePostEnabled = true)
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
                            .requestMatchers(GET,
                                    String.format("%s/clients", apiPrefix),
                                    String.format("%s/clients/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/roles", apiPrefix),
                                    String.format("%s/roles/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/images", apiPrefix),
                                    String.format("%s/images/**", apiPrefix)).permitAll()
                            .requestMatchers(POST,
                                    String.format("%s/posts/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PATCH,
                                    String.format("%s/posts/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(DELETE,
                                    String.format("%s/posts/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST,
                                    String.format("%s/categories/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT,
                                    String.format("%s/categories/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PATCH,
                                    String.format("%s/categories/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(DELETE,
                                    String.format("%s/categories/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(POST,
                                    String.format("%s/clients/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PUT,
                                    String.format("%s/clients/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(PATCH,
                                    String.format("%s/clients/**", apiPrefix)).hasRole("ADMIN")
                            .requestMatchers(DELETE,
                                    String.format("%s/clients/**", apiPrefix)).hasRole("ADMIN")
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable);

        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);
            }
                });
        return http.build();
    }

}
