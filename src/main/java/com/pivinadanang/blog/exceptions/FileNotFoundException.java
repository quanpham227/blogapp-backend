package com.pivinadanang.blog.exceptions;

public class FileNotFoundException extends RuntimeException {
    // Constructor không tham số
    public FileNotFoundException() {
        super();
    }

    // Constructor với thông báo lỗi
    public FileNotFoundException(String message) {
        super(message);
    }

    // Constructor với thông báo lỗi và nguyên nhân
    public FileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor với nguyên nhân
    public FileNotFoundException(Throwable cause) {
        super(cause);
    }
}
