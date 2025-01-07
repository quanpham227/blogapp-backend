package com.pivinadanang.blog.config.test;

import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@Configuration
public class TestEmailConfig {

    @Bean
    public JavaMailSender mockJavaMailSender() {
        JavaMailSender mockMailSender = mock(JavaMailSender.class);
        doNothing().when(mockMailSender).send((MimeMessage) any());
        return mockMailSender;
    }
}
