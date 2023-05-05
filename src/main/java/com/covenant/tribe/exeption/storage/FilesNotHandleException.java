package com.covenant.tribe.exeption.storage;

public class FilesNotHandleException extends RuntimeException{

    public FilesNotHandleException(String message) {
        super(message);
    }

    public FilesNotHandleException(String message, Throwable cause) {
        super(message, cause);
    }
}
