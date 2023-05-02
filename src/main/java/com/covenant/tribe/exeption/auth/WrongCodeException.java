package com.covenant.tribe.exeption.auth;

public class WrongCodeException extends RuntimeException {
    public WrongCodeException() {
        super();
    }

    public WrongCodeException(String message) {
        super(message);
    }
}
