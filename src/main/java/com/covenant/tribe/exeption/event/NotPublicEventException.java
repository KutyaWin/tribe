package com.covenant.tribe.exeption.event;

public class NotPublicEventException extends RuntimeException {
    public NotPublicEventException(String message) {
        super(message);
    }
}
