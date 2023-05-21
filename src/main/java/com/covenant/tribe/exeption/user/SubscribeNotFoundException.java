package com.covenant.tribe.exeption.user;

public class SubscribeNotFoundException extends RuntimeException {
    public SubscribeNotFoundException(String message) {
        super(message);
    }
}
