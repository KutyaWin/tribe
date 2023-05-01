package com.covenant.tribe.exeption.auth;

public class GoogleIntrospectionException extends RuntimeException{
    public GoogleIntrospectionException(String message) {
        super(message);
    }

    public GoogleIntrospectionException() {
        super();
    }
}
