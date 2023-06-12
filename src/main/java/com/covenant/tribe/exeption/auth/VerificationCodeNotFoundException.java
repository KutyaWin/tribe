package com.covenant.tribe.exeption.auth;

public class VerificationCodeNotFoundException extends RuntimeException {
    public VerificationCodeNotFoundException(String message) {
        super(message);
    }
}
