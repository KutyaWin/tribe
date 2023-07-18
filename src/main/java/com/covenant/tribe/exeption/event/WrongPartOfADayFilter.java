package com.covenant.tribe.exeption.event;

public class WrongPartOfADayFilter extends RuntimeException {
    public WrongPartOfADayFilter(String message) {
        super(message);
    }
}