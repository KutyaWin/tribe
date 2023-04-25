package com.covenant.tribe.exeption;

public class MissingRequestHeaderException extends RuntimeException {
    public MissingRequestHeaderException(String message) {
        super(message);
    }
}
