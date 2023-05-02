package com.covenant.tribe.exeption;

import com.covenant.tribe.dto.ResponseErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(AlreadyExistArgumentForAddToEntityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseErrorDTO handleUserExistException(AlreadyExistArgumentForAddToEntityException e) {
        log.error("[EXCEPTION] message: " + e.getMessage());

        return ResponseErrorDTO.builder()
                .status(HttpStatus.CONFLICT)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        log.error("[EXCEPTION] message: " + e.getMessage());

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }

    @ExceptionHandler(MailException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseErrorDTO handleMailExceptionException(MailException e) {
        String message = String.format("Mail don't send because: %s'", e.getMessage());
        log.error("[EXCEPTION] message: " + message);

        return ResponseErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorMessage(List.of(message))
                .build();
    }
}
