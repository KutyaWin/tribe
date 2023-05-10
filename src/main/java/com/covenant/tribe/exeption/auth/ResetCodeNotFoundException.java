package com.covenant.tribe.exeption.auth;

public class ResetCodeNotFoundException extends RuntimeException {
    public ResetCodeNotFoundException(String message) {
        super(message);
    }
}
