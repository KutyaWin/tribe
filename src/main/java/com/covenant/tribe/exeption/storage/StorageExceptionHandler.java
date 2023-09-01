package com.covenant.tribe.exeption.storage;

import com.covenant.tribe.dto.ResponseErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class StorageExceptionHandler {

    @ExceptionHandler(FilesNotHandleException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseErrorDTO handleFileNotSavedException(RuntimeException fileNotSavedException) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorMessage(List.of(fileNotSavedException.getMessage()))
                .build();
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseErrorDTO handleFileNotFoundException(Exception fileNotFoundException) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.NOT_FOUND)
                .errorMessage(List.of(fileNotFoundException.getMessage()))
                .build();
    }

    @ExceptionHandler(FileAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseErrorDTO handleFileAlreadyExistsException(FileAlreadyExistsException fileAlreadyExistsException) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.CONFLICT)
                .errorMessage(List.of(fileAlreadyExistsException.getMessage()))
                .build();
    }
}
