package com.pivinadanang.blog.controllers;

import com.pivinadanang.blog.controller.ContactController;
import com.pivinadanang.blog.dtos.ContactDTO;
import com.pivinadanang.blog.responses.ResponseObject;
import com.pivinadanang.blog.services.contact.IEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class ContactControllerTest {

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private ContactController contactController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(contactController, "recipientEmail", "test@example.com");
    }

    @Test
    public void testSendContactEmail() {
        ContactDTO contactForm = new ContactDTO();
        contactForm.setName("John Doe");
        contactForm.setEmail("john.doe@example.com");
        contactForm.setSubject("Test Subject");
        contactForm.setMessage("Test Message");

        String to = "test@example.com";
        String subject = "New Contact Form Submission";
        String text = "Name: " + contactForm.getName() + "\n" +
                "Email: " + contactForm.getEmail() + "\n" +
                "Subject: " + contactForm.getSubject() + "\n" +
                "Message: " + contactForm.getMessage();
        String from = contactForm.getEmail();

        doNothing().when(emailService).sendEmail(to, subject, text, from);

        ResponseEntity<ResponseObject> responseEntity = contactController.sendContactEmail(contactForm);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Thank you for your message. We will get back to you soon.", responseEntity.getBody().getMessage());

        verify(emailService).sendEmail(to, subject, text, from);
    }
}