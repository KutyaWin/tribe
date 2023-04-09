package com.covenant.tribe.exeption.event;

import com.covenant.tribe.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class EventExceptionHandler {

    @ExceptionHandler(EventTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(
            EventTypeNotFoundException eventTypeNotFoundException
    ) {
        String message = String.format(
          "Event type with %s  does not exist",
          eventTypeNotFoundException.getMessage()
        );
        log.error(message);
        return ResponseEntity
                .badRequest()
                .body(
                        new ErrorResponse(message)
                );
    }

}
