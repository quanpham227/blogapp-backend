package com.pivinadanang.blog.services.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService implements IEmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String adminEmail;

    @Autowired
    public EmailService(JavaMailSender mailSender, @Value("${app.contact.recipient-email}") String adminEmail) {
        this.mailSender = mailSender;
        this.adminEmail = adminEmail;
    }

    @Override
    public void sendEmail(String to, String subject, String text, String from) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom(from);

        try {
            mailSender.send(message);
        } catch (MailAuthenticationException e) {
            logger.error("Authentication error: {}", e.getMessage());
            notifyAdmin("Authentication error", e.getMessage());
        } catch (Exception e) {
            logger.error("Error sending email: {}", e.getMessage());
            // Retry sending the email
            try {
                mailSender.send(message);
            } catch (Exception retryException) {
                logger.error("Retry failed: {}", retryException.getMessage());
                notifyAdmin("Email sending failed", retryException.getMessage());
            }
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
            logger.error("Error notifying admin: {}", e.getMessage());
        }
    }
}