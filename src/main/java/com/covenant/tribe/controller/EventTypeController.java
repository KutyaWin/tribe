package com.covenant.tribe.controller;

import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.service.EventTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            description = "Like me screen. Get all dark all light rectangle event types.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = EventTypeDTO.class)))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @GetMapping("/event/type/rectangle")
    public ResponseEntity<?> getRectangleEventTypes(
            @RequestParam(value = "is_dark") boolean isDark
    ) {
        List<EventTypeDTO> eventTypeDTOs = eventTypeService.getAllRectangleEventTypes(isDark);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventTypeDTOs);
    }

    @Operation(
            tags = "EventType",
            description = "Screen: создание события. Get all dark or light circle event types.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = EventTypeDTO.class)))}
    )
    @GetMapping("/event/type/circle")
    public ResponseEntity<?> getCircleEventTypes(
            @RequestParam(value = "is_dark") boolean isDark
    ) {
        List<EventTypeDTO> eventTypeDTOs = eventTypeService.getAllCircleEventTypes(isDark);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventTypeDTOs);
    }
}
