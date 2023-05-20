package com.covenant.tribe.exeption.event;

public class NotClosedEventException extends RuntimeException {
    public NotClosedEventException(String message) {
        super(message);
    }
}
