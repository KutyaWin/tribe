package com.covenant.tribe.exeption.event;

public class EventAlreadyExistException extends RuntimeException {
    public EventAlreadyExistException(String message) {
        super(message);
    }
}
