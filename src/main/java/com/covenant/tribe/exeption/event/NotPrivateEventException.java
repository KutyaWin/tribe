package com.covenant.tribe.exeption.event;

public class NotPrivateEventException extends RuntimeException {
    public NotPrivateEventException(String message) {
        super(message);
    }
}
