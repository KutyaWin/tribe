package com.covenant.tribe.exeption.storage;

public class FileNotSavedException extends RuntimeException{

    public FileNotSavedException(String message) {
        super(message);
    }
}
