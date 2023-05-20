package com.covenant.tribe.exeption.event;

public class UserAlreadyParticipantException extends RuntimeException {
    public UserAlreadyParticipantException(String message) {
        super(message);
    }
}
