package com.covenant.tribe.exeption.event;

public class MessageDidntSendException extends RuntimeException {
    public MessageDidntSendException(String errMessage) {
        super(errMessage);
    }
}
