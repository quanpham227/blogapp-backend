package com.pivinadanang.blog.controller;


import com.pivinadanang.blog.dtos.ContactDTO;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.services.contact.IEmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
public class ContactController {

    @Autowired
    private IEmailService emailService;

    @Value("${app.contact.recipient-email}")
    private String recipientEmail;

    // src/main/java/com/pivinadanang/blog/controller/ContactController.java

// src/main/java/com/pivinadanang/blog/controller/ContactController.java

    // src/main/java/com/pivinadanang/blog/controller/ContactController.java

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<ResponseObject> sendContactEmail(@Valid @RequestBody ContactDTO contactForm) {
        if (contactForm.getEmail() == null || contactForm.getSubject() == null || contactForm.getMessage() == null) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .message("Missing required fields")
                            .build()
            );
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!contactForm.getEmail().matches(emailRegex)) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .data(null)
                            .message("Invalid email format")
                            .build()
            );
        }

        String to = recipientEmail; // Địa chỉ email nhận thông tin liên hệ từ cấu hình
        String subject = "New Contact Form Submission";
        String text = "Name: " + contactForm.getName() + "\n" +
                "Email: " + contactForm.getEmail() + "\n" +
                "Subject: " + contactForm.getSubject() + "\n" +
                "Message: " + contactForm.getMessage();
        String from = contactForm.getEmail();

        try {
            emailService.sendEmail(to, subject, text, from);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseObject.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .data(null)
                            .message("Failed to send email. Please try again later.")
                            .build()
            );
        }

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(null)
                        .message("Thank you for your message. We will get back to you soon.")
                        .build()
        );
    }

}