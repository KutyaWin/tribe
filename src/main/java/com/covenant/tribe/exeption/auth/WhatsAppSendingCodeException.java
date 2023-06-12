package com.covenant.tribe.exeption.auth;

public class WhatsAppSendingCodeException extends RuntimeException{
    public WhatsAppSendingCodeException(String message) {
        super(message);
    }

    public WhatsAppSendingCodeException() {
        super();
    }
}
