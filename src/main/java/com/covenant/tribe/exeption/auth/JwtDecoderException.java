package com.covenant.tribe.exeption.auth;

public class JwtDecoderException extends RuntimeException{
    public JwtDecoderException(String message) {
        super(message);
    }
}
