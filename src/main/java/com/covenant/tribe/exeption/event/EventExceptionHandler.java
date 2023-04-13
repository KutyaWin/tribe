package com.covenant.tribe.exeption.event;

import com.covenant.tribe.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class EventExceptionHandler {

    @ExceptionHandler({EventTypeNotFoundException.class, EventNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleException(RuntimeException runtimeException) {

        log.error("[EXCEPTION] message: " + runtimeException.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .errorMessage(List.of(runtimeException.getMessage()))
                .build();
    }
}
