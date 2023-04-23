package com.covenant.tribe.controller;

import com.covenant.tribe.dto.ImageDTO;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.storage.TempFileDTO;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.PhotoStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileNotFoundException;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/events")
public class EventController {

    EventService eventService;
    PhotoStorageService storageService;

    @Operation(
            tags = "Event",
            description = "CreateEvent screen. Create a new event by body. Response eventId.",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = RequestTemplateForCreatingEventDTO.class)))})
    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestBody @Valid RequestTemplateForCreatingEventDTO requestTemplateForCreatingEventDTO
    ) {
        log.info("[CONTROLLER] start endpoint createEvent with RequestBody: {}", requestTemplateForCreatingEventDTO);

        Long response = eventService.saveNewEvent(requestTemplateForCreatingEventDTO).getId();

        log.info("[CONTROLLER] end endpoint createEvent with response: {}", response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(
            tags = "Event",
            description = "CardBig screen. Get a event by event_id and user_id.",
            responses = {
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = DetailedEventInSearchDTO.class)))})
    @GetMapping("/{event_id}")
    public ResponseEntity<?> getEventById(
            @PathVariable("event_id") String eventId,
            @RequestParam(value = "user-id", required = false) Long userId
    ) {
        log.info("[CONTROLLER] start endpoint getEventById with param: {}, {}", eventId, userId);

        DetailedEventInSearchDTO responseEvent = eventService.getDetailedEventById(Long.parseLong(eventId), userId);

        log.info("[CONTROLLER] end endpoint getEventById with response: {}", responseEvent);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseEvent);
    }

    @PostMapping("/{event_id}/{user_id}")
    public ResponseEntity<?> addUserToEventAsParticipant(
            @PathVariable("event_id") Long eventId,
            @PathVariable("user_id") Long userId
    ) {
        eventService.addUserToEventAsParticipant(eventId, userId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @PostMapping("/avatars")
    public ResponseEntity<?> addEventAvatarToTempDirectory(
            @RequestBody ImageDTO imageDTO
    ) {
        String uniqueTempFileName = storageService.saveFileToTmpDir(imageDTO.getContentType(), imageDTO.getImage());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new TempFileDTO(uniqueTempFileName));
    }

    @GetMapping("/avatars/{added_date}/{avatar_file_name}")
    public ResponseEntity<?> getEventAvatar(
            @PathVariable(value = "added_date") String addedDate,
            @PathVariable(value = "avatar_file_name") String avatarFileName
    ) throws FileNotFoundException {
        ImageDTO imageDTO = storageService.getEventAvatar(addedDate + "/" + avatarFileName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(imageDTO.getContentType()))
                .body(imageDTO.getImage());
    }


}
