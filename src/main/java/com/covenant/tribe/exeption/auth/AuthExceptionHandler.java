package com.covenant.tribe.exeption.auth;

import com.covenant.tribe.dto.ResponseErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(JwtDecoderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseErrorDTO handleJwtDecoderException(JwtDecoderException e) {
        log.error("[EXCEPTION] message: " + e.getMessage());

        return ResponseErrorDTO.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }

    @ExceptionHandler(UnexpectedTokenTypeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleJwtDecoderException(UnexpectedTokenTypeException e) {
        String message = String.format("Token with %s token type does not exist", e.getMessage());
        log.error("[EXCEPTION] message: " + message);

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(message))
                .build();
    }

    @ExceptionHandler(VkIntrospectionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseErrorDTO handleVkIntrospectionException(VkIntrospectionException e) {
        String message = String.format("Token is invalid because vk return error with message: %s", e.getMessage());
        log.error("[EXCEPTION] message: " + message);

        return ResponseErrorDTO.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errorMessage(List.of(message))
                .build();
    }

    @ExceptionHandler(GoogleIntrospectionException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseErrorDTO handleGoogleIntrospectionException(GoogleIntrospectionException e) {
        String message = String.format("Token is invalid because google return error with message: %s", e.getMessage());
        log.error("[EXCEPTION] message: " + message);

        return ResponseErrorDTO.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errorMessage(List.of(message))
                .build();
    }

    @ExceptionHandler(ExpiredCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleGoogleIntrospectionException(ExpiredCodeException e) {
        String message = String.format("Code %s is expired", e.getMessage());
        log.error("[EXCEPTION] message: " + message);

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(message))
                .build();
    }

    @ExceptionHandler(MakeTokenException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseErrorDTO handleMakeTokenException(MakeTokenException e) {
        String message = String.format("Something went wrong during making token: %s", e.getMessage());
        log.error("[EXCEPTION] message: " + message);

        return ResponseErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorMessage(List.of(message))
                .build();
    }

    @ExceptionHandler(WrongCodeException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseErrorDTO handleWrongCodeException(WrongCodeException e) {
        log.error("[EXCEPTION] message: " + e.getMessage());

        return ResponseErrorDTO.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseErrorDTO handleBadCredentialsException(BadCredentialsException e) {
        log.error("[EXCEPTION] message: " + e.getMessage());

        return ResponseErrorDTO.builder()
                .status(HttpStatus.NOT_FOUND)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }

}
