package com.pivinadanang.blog.exceptions;

import org.springframework.validation.BindingResult;

public class MethodArgumentNotValidException extends RuntimeException {
    private final BindingResult bindingResult;

    public MethodArgumentNotValidException(String message, BindingResult bindingResult) {
        super(message);
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}