package com.pivinadanang.blog.configurations;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Chuyển hướng tất cả các yêu cầu không khớp với tệp tĩnh hoặc API đến index.html
        registry.addViewController("/{spring:(?!api|static).*}")
                .setViewName("forward:/index.html");
        registry.addViewController("/{spring:(?!api|static).*}/**")
                .setViewName("forward:/index.html");
    }
}
