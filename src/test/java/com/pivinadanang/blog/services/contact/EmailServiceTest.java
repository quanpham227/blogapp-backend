package com.pivinadanang.blog.services.contact;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private String to;
    private String subject;
    private String text;
    private String from;
    private String adminEmail;

    @BeforeEach
    void setUp() {
        to = "test@example.com";
        subject = "Test Subject";
        text = "Test Text";
        from = "from@example.com";
        adminEmail = "admin@example.com";
        emailService = new EmailService(mailSender, adminEmail);
    }

    @Test
    void testSendEmail_Success() {
        // Ghi đè phương thức chính xác để tránh lỗi mơ hồ
        doAnswer(invocation -> {
            SimpleMailMessage message = invocation.getArgument(0);
            return null; // Không làm gì (giả lập hành vi gửi email thành công)
        }).when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(to, subject, text, from);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_AuthenticationError() {
        // Ghi đè lỗi MailAuthenticationException
        doThrow(new MailAuthenticationException("Authentication error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(to, subject, text, from);

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
        verify(mailSender, times(1)).send((SimpleMailMessage) argThat(message -> ((SimpleMailMessage) message).getTo()[0].equals(adminEmail)));
    }
    @Test
    void testSendEmail_GeneralError() {
        // Mock RuntimeException
        doThrow(new RuntimeException("General error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(to, subject, text, from);

        // Verify that the send method was called twice for the original email
        verify(mailSender, times(2)).send((SimpleMailMessage) argThat(message -> !((SimpleMailMessage) message).getTo()[0].equals(adminEmail)));
        // Verify that the send method was called once for the admin notification
        verify(mailSender, times(1)).send((SimpleMailMessage) argThat(message -> ((SimpleMailMessage) message).getTo()[0].equals(adminEmail)));
    }
}
