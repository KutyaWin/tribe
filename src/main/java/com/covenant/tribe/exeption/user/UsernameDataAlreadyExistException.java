package com.covenant.tribe.exeption.user;


public class UsernameDataAlreadyExistException extends RuntimeException {
    public UsernameDataAlreadyExistException(String message) {
        super(message);
    }
}
