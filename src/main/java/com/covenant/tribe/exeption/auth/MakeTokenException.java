package com.covenant.tribe.exeption.auth;

public class MakeTokenException extends RuntimeException{
    public MakeTokenException() {
        super();
    }

    public MakeTokenException(String message) {
        super(message);
    }
}
