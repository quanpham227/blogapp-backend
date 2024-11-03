package com.pivinadanang.blog.services.contact;

public interface IEmailService {
    void sendEmail(String to, String subject, String text, String from);

}
