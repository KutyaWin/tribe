package com.covenant.tribe.exeption.event;

public class UserAlreadyInvitedException extends RuntimeException {
    public UserAlreadyInvitedException(String message) {
        super(message);
    }
}
