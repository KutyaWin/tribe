package com.covenant.tribe.controller;

import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.service.EventTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1")
public class EventTypeController {

    EventTypeService eventTypeService;

    @Operation(
            tags = "EventType",
            description = "Like me screen. Get all event types.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = EventTypeDTO.class)))}
    )
    @GetMapping("/event/type")
    public ResponseEntity<?> getAllEventTypes() {
        List<EventTypeDTO> eventTypeDTOs = eventTypeService.getAllEventTypes();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventTypeDTOs);
    }

    //todo: fetch all tags by event type name

}
