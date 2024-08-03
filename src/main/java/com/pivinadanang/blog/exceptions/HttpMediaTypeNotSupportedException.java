package com.pivinadanang.blog.exceptions;

public class HttpMediaTypeNotSupportedException extends RuntimeException {
    public HttpMediaTypeNotSupportedException(String message) {
        super(message);
    }
}
