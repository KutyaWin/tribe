package com.covenant.tribe.exeption.event;

import com.covenant.tribe.dto.ResponseErrorDTO;
import com.covenant.tribe.exeption.event.search.EventSearchUnitNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class EventExceptionHandler {

    @ExceptionHandler({EventTypeNotFoundException.class, EventNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseErrorDTO handleException(RuntimeException runtimeException) {

        log.error("[EXCEPTION] message: " + runtimeException.getMessage());

        return ResponseErrorDTO.builder()
                .status(HttpStatus.NOT_FOUND)
                .errorMessage(List.of(runtimeException.getMessage()))
                .build();
    }
    @ExceptionHandler(MessageDidntSendException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseErrorDTO handleMessageDidntSendException(MessageDidntSendException messageDidntSendException) {
        System.out.println("this is messageDidntSendException");
        return ResponseErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .errorMessage(List.of(messageDidntSendException.getMessage()))
                .build();
    }

    @ExceptionHandler(EventAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseErrorDTO handleEventAlreadyExistException(EventAlreadyExistException eventAlreadyExistException) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.CONFLICT)
                .errorMessage(List.of(eventAlreadyExistException.getMessage()))
                .build();
    }

    @ExceptionHandler(EventAlreadyVerifiedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseErrorDTO handleEventAlreadyExistException(EventAlreadyVerifiedException eventAlreadyVerifiedException) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.CONFLICT)
                .errorMessage(List.of(eventAlreadyVerifiedException.getMessage()))
                .build();
    }

    @ExceptionHandler(UserRelationsWithEventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseErrorDTO handleUserRelationsWithEventNotFoundException(
            UserRelationsWithEventNotFoundException eventNotVerifiedException
    ) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.NOT_FOUND)
                .errorMessage(List.of(eventNotVerifiedException.getMessage()))
                .build();
    }

    @ExceptionHandler(EventNotVerifiedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleEventAlreadyExistException(EventNotVerifiedException eventNotVerifiedException) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(eventNotVerifiedException.getMessage()))
                .build();
    }

    @ExceptionHandler(NotPrivateEventException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleNotClosedEventException(NotPrivateEventException notPrivateEventException) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(notPrivateEventException.getMessage()))
                .build();
    }

    @ExceptionHandler(NotPublicEventException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleNotPublicEventException(NotPublicEventException notPublicEventException) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(notPublicEventException.getMessage()))
                .build();
    }
    @ExceptionHandler(UserAlreadyParticipantException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleUserAlreadyInvitedException(UserAlreadyParticipantException participantException) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(participantException.getMessage()))
                .build();
    }

    @ExceptionHandler(UserAlreadyInvitedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleUserAlreadyInvitedException(UserAlreadyInvitedException e) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(e.getMessage()))
                .build();
    }

    @ExceptionHandler(UserAlreadySendRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleUserAlreadySendRequestException(
            UserAlreadySendRequestException userAlreadySendRequestException
    ) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(userAlreadySendRequestException.getMessage()))
                .build();
    }

    @ExceptionHandler(EventSortingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleBadSortingRequest(EventSortingException badSortingEx) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(badSortingEx.getMessage()))
                .build();
    }

    @ExceptionHandler(EventSearchUnitNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseErrorDTO handleSearchUnitException(RuntimeException exception) {

        return ResponseErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST)
                .errorMessage(List.of(exception.getMessage()))
                .build();
    }
}
