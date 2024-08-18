package com.pivinadanang.blog.exceptions;

public class CloudinaryException extends RuntimeException{

    private String publicId;

    public CloudinaryException(String message) {
        super(message);
    }

    public CloudinaryException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudinaryException(String message, String publicId) {
        super(message);
        this.publicId = publicId;
    }

    public String getPublicId() {
        return publicId;
    }
}
