package com.covenant.tribe.exeption.event;

public class EventAlreadyVerifiedException extends RuntimeException {
    public EventAlreadyVerifiedException(String message) {
        super(message);
    }
}
