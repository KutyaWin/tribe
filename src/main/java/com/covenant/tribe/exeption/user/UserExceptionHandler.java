package com.covenant.tribe.exeption.user;

import com.covenant.tribe.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(
            RuntimeException userNotFoundException
    ) {
        String message = String.format(
                "User with id %s does not exist", userNotFoundException.getMessage()
        );

        log.error("[EXCEPTION] message: " + message);

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(message))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgNotValidException(MethodArgumentNotValidException ex) {
        List<String> errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(fE -> fE.getDefaultMessage()).toList();

        log.error("[EXCEPTION] message: " + errorList);

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(errorList)
                .build();
    }

}
