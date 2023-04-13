package com.covenant.tribe.exeption.user;

import com.covenant.tribe.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleException(
            RuntimeException userNotFoundException
    ) {
        String message = String.format(
                "User with id %s does not exist", userNotFoundException.getMessage()
        );
        log.error(message);
        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorResponse(
                                LocalDateTime.now().toString(),
                                "NOT FOUND",
                                message
                        )
                );
    }

}
