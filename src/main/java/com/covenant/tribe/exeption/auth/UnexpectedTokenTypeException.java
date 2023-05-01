package com.covenant.tribe.exeption.auth;

public class UnexpectedTokenTypeException extends RuntimeException{
    public UnexpectedTokenTypeException(String message) {
        super(message);
    }

    public UnexpectedTokenTypeException() {
        super();
    }
}
