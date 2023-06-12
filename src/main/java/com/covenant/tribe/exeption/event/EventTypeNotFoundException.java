package com.covenant.tribe.exeption.event;

public class EventTypeNotFoundException extends RuntimeException{

    public EventTypeNotFoundException(String message) {
        super(message);
    }
}
