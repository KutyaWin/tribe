package com.covenant.tribe.exeption.scheduling;

import com.covenant.tribe.dto.ResponseErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class SchedulingExceptionHandler {

    @ExceptionHandler(BroadcastNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseErrorDTO handleBroadcastNotFoundException(BroadcastNotFoundException e) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.NOT_FOUND)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }

    @ExceptionHandler(TriggerNotUpdatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleTriggerNotUpdatedException(TriggerNotUpdatedException e) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }
}
