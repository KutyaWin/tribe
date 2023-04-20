package com.covenant.tribe.exeption.storage;

import com.covenant.tribe.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class StorageExceptionHandler {

    @ExceptionHandler(FileNotSavedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleFileNotSavedException(RuntimeException fileNotSavedException) {
        log.error("[EXCEPTION] message: " + fileNotSavedException.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorMessage(List.of(fileNotSavedException.getMessage()))
                .build();
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFileNotFoundException(Exception fileNotFoundException) {
        log.error("[EXCEPTION] message: " + fileNotFoundException.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .errorMessage(List.of(fileNotFoundException.getMessage()))
                .build();
    }
}
