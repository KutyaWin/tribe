package com.covenant.tribe.exeption.auth;

public class RegistrantNotFoundException extends RuntimeException {
    public RegistrantNotFoundException(String message) {
        super(message);
    }
}
