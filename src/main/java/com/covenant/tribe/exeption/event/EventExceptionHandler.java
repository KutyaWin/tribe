package com.covenant.tribe.exeption.event;

import com.covenant.tribe.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class EventExceptionHandler {

    @ExceptionHandler({EventTypeNotFoundException.class, EventNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleException(
            RuntimeException runtimeException
    ) {
        log.error(runtimeException.getMessage());
        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorResponse(
                                LocalDateTime.now().toString(),
                                "NOT FOUND",
                                runtimeException.getMessage()
                        )
                );
    }

}
