package com.covenant.tribe.exeption.user;

import com.covenant.tribe.dto.ResponseErrorDTO;
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
    public ResponseErrorDTO handleUserNotFoundException(
            RuntimeException userNotFoundException
    ) {
        String message = String.format(
                "User with id %s does not exist", userNotFoundException.getMessage()
        );

        log.error("[EXCEPTION] message: " + message);

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(message))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleMethodArgNotValidException(MethodArgumentNotValidException ex) {
        List<String> errorList = ex.getBindingResult().getFieldErrors().stream()
                .map(fE -> fE.getDefaultMessage()).toList();

        log.error("[EXCEPTION] message: " + errorList);

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(errorList)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseErrorDTO handleUserExistException(UsernameDataAlreadyExistException e) {
        log.error("[EXCEPTION] message: " + e.getMessage());

        return ResponseErrorDTO.builder()
                .status(HttpStatus.CONFLICT)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }
}
