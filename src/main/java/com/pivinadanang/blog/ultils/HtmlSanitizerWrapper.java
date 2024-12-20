package com.pivinadanang.blog.ultils;


import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizerWrapper {
    public String sanitize(String input) {
        return HtmlSanitizer.sanitize(input);
    }
}