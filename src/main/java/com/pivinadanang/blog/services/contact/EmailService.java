package com.pivinadanang.blog.services.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements IEmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.contact.recipient-email}")
    private String adminEmail;

    @Override
    public void sendEmail(String to, String subject, String text, String from) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom(from);
            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            // Lỗi xác thực, có thể do mật khẩu sai
            System.err.println("Authentication error: " + e.getMessage());
            notifyAdmin("Authentication Error", "Could not send email: " + e.getMessage());
        } catch (Exception e) {
            // Các lỗi khác khi gửi email
            System.err.println("Error sending email: " + e.getMessage());
            notifyAdmin("Email Sending Error", "Error sending email: " + e.getMessage());
        }
    }

    private void notifyAdmin(String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // Xử lý khi không thể thông báo cho admin, ví dụ ghi log
            System.err.println("Error notifying admin: " + e.getMessage());
        }
    }
}
