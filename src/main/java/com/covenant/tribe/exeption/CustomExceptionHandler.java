package com.covenant.tribe.exeption;

import com.covenant.tribe.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(AlreadyExistArgumentForAddToEntityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUserExistException(AlreadyExistArgumentForAddToEntityException e) {
        log.error("[EXCEPTION] message: " + e.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }
}
