package com.covenant.tribe.exeption.event;

import org.springframework.stereotype.Component;

public class EventTypeNotFoundException extends RuntimeException{

    public EventTypeNotFoundException(String message) {
        super(message);
    }
}
