package com.covenant.tribe.exeption;

public class AlreadyExistArgumentForAddToEntityException extends RuntimeException {
        public AlreadyExistArgumentForAddToEntityException(String message) {
            super(message);
        }
}
