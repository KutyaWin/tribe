package com.covenant.tribe.exeption.auth;

public class ExpiredCodeException extends RuntimeException {
    public ExpiredCodeException() {
        super();
    }

    public ExpiredCodeException(String message) {
        super(message);
    }
}
