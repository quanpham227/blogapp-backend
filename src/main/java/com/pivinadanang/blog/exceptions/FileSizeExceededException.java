package com.pivinadanang.blog.exceptions;

public class FileSizeExceededException  extends RuntimeException{
    public FileSizeExceededException(String message) {
        super(message);
    }

    public FileSizeExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
