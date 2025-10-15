package com.example.Social_Media_Portal.Exception;


public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException (String msg) {
        super(msg);
    }

    public PostNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostNotFoundException(Throwable cause) {
        super(cause);
    }
}
