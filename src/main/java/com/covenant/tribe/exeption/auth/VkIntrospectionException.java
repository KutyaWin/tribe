package com.covenant.tribe.exeption.auth;

public class VkIntrospectionException extends RuntimeException{
    public VkIntrospectionException(String message) {
        super(message);
    }

    public VkIntrospectionException() {
        super();
    }
}
