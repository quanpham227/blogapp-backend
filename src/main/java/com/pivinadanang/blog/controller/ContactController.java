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

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR')")
    public ResponseEntity<ResponseObject> sendContactEmail(@Valid @RequestBody ContactDTO contactForm) {
        String to = recipientEmail; // Địa chỉ email nhận thông tin liên hệ từ cấu hình
        String subject = "New Contact Form Submission";
        String text = "Name: " + contactForm.getName() + "\n" +
                "Email: " + contactForm.getEmail() + "\n" +
                "Subject: " + contactForm.getSubject() + "\n" +
                "Message: " + contactForm.getMessage();
        String from = contactForm.getEmail();

        emailService.sendEmail(to, subject, text, from);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(HttpStatus.OK)
                        .data(null)
                        .message("Thank you for your message. We will get back to you soon.")
                        .build()
        );
    }
}