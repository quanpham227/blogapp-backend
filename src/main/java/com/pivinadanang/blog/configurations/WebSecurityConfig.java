package com.pivinadanang.blog.configurations;
import com.pivinadanang.blog.filters.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;



import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
@RequiredArgsConstructor
class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception{
        http
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    //healthcheck
                                    String.format("%s/healthcheck/**", apiPrefix),

                                    //swagger
                                    //"/v3/api-docs",
                                    //"/v3/api-docs/**",
                                    "/api-docs",
                                    "/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/webjars/swagger-ui/**",
                                    "/swagger-ui/index.html",

                                    //Google login
                                    "users/auth/social-login",
                                    "users/auth/social/callback"

                            )
                            .permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/roles**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/categories/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/user/posts/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/clients/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/comments/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/images/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/slides/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/about/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/tags/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/achievements/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/dashboard/**", apiPrefix)).permitAll()
                            .anyRequest()
                            .authenticated();
                    //.anyRequest().permitAll();
                })
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .headers(headers -> headers
                        .defaultsDisabled()
                        .contentTypeOptions(contentTypeOptions -> contentTypeOptions.disable())
                        .frameOptions(frameOptions -> frameOptions.deny())
                        .xssProtection(xss -> xss.disable()) // Disable XSS protection
                        .cacheControl(cache -> cache.disable())
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'"))
                );
        http.securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()));
        return http.build();
    }
}
