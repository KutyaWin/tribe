package com.covenant.tribe.exeption.event;

public class UserAlreadySendRequestException extends RuntimeException {
    public UserAlreadySendRequestException(String message) {
        super(message);
    }
}
