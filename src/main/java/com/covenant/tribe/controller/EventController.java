package com.covenant.tribe.controller;

import com.covenant.tribe.dto.ImageDTO;
import com.covenant.tribe.dto.PageResponse;
import com.covenant.tribe.dto.ResponseErrorDTO;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.EventInUserProfileDTO;
import com.covenant.tribe.dto.event.EventVerificationDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.event.SearchEventDTO;
import com.covenant.tribe.dto.storage.TempFileDTO;
import com.covenant.tribe.security.JwtProvider;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.PhotoStorageService;
import com.covenant.tribe.util.mapper.EventMapper;
import com.covenant.tribe.util.querydsl.EventFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@Tag(name = "Event")
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/events")
public class EventController {

    EventService eventService;
    PhotoStorageService storageService;

    JwtProvider jwtProvider;

    @Operation(
            description = "CreateEvent screen. Create a new event by body. Response eventId.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = DetailedEventInSearchDTO.class))),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Some data is not valid, please check it.",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseErrorDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = "{" +
                                                            "\"time\":\"2022-01-01T00:00:00\"," +
                                                            "\"status\":\"400 Bad Request\"," +
                                                            " \"error_message\":[\"string\"]" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Some data is not found.",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseErrorDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = "{" +
                                                            "\"time\":\"2022-01-01T00:00:00\"," +
                                                            "\"status\":\"404 Not Found\"," +
                                                            " \"error_message\":[\"string\"]" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Some data is not unique, duplicate, or not valid.",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseErrorDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = "{" +
                                                            "\"time\":\"2022-01-01T00:00:00\"," +
                                                            "\"status\":\"409 Conflict\"," +
                                                            " \"error_message\":[\"string\"]" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error in server.",
                            content = @Content(
                                    schema = @Schema(implementation = ResponseErrorDTO.class),
                                    examples = {
                                            @ExampleObject(
                                                    value = "{" +
                                                            "\"time\":\"2022-01-01T00:00:00\"," +
                                                            "\"status\":\"500 Internal Server Error\"," +
                                                            " \"error_message\":[\"string\"]" +
                                                            "}"
                                            )
                                    }
                            )
                    ),
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PostMapping
    @PreAuthorize("#requestTemplateForCreatingEventDTO.getOrganizerId().toString().equals(authentication.name)")
    public ResponseEntity<?> createEvent(
            @RequestBody RequestTemplateForCreatingEventDTO requestTemplateForCreatingEventDTO
    ) throws FileNotFoundException {
        log.info("[CONTROLLER] start endpoint createEvent with RequestBody: {}", requestTemplateForCreatingEventDTO);

        DetailedEventInSearchDTO response = eventService.handleNewEvent(requestTemplateForCreatingEventDTO);

        log.info("[CONTROLLER] end endpoint createEvent with response: {}", response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(
            description = "CardBig screen. Get an event by event_id and user_id.",
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

    @Operation(
            description = "Screen: none. Get events which has status VERIFICATION_PENDING",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = EventVerificationDTO.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @GetMapping("/verification")
    public ResponseEntity<?> getEventWithVerificationPendingStatus() {
        log.info("[CONTROLLER] start endpoint getEventWithVerificationPendingStatus");

        List<EventVerificationDTO> events = eventService.getEventWithVerificationPendingStatus();

        log.info("[CONTROLLER] end endpoint getEventWithVerificationPendingStatus");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events);
    }

    @Operation(
            description = "Screen: none. Update event status to PUBLISHED",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PatchMapping("/verification/confirm/{event_id}")
    public ResponseEntity<?> updateEventStatusToPublished(
        @PathVariable(value = "event_id") Long eventId
    ) {
        log.info("[CONTROLLER] start endpoint updateEventStatusToPublished with param: {}", eventId);

        eventService.updateEventStatusToPublished(eventId);

        log.info("[CONTROLLER] end endpoint updateEventStatusToPublished");
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(
            description = "Screen: none. Update event status to SEND_TO_REWORK",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PatchMapping("/verification/rework/{event_id}")
    public ResponseEntity<?> updateEventStatusToSendToRework(
            @PathVariable(value = "event_id") Long eventId
    ) {
        log.info("[CONTROLLER] start endpoint updateEventStatusToSendToRework with param: {}", eventId);

        eventService.updateEventStatusToSendToRework(eventId);

        log.info("[CONTROLLER] end endpoint updateEventStatusToSendToRework");
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }


    @Operation(
            description = "Screen: Профиль ADMIN. Get events which user is the organizer",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = EventInUserProfileDTO.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#organizerId.equals(authentication.getName())")
    @GetMapping("/organisation/{organizer_id}")
    public ResponseEntity<?> findEventsByOrganizerId(@PathVariable(value = "organizer_id") String organizerId) {
        log.info("[CONTROLLER] start endpoint findEventsByOrganizerId with param: {}", organizerId);
        List<EventInUserProfileDTO> organizersEvents = eventService.findEventsByOrganizerId(organizerId);

        log.info("[CONTROLLER] end endpoint findEventsByOrganizerId with response: {}", organizersEvents);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(organizersEvents);
    }

    @Operation(
            description = "Screen: Профиль ADMIN, профиль USER. Get events which user is invited",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = EventInUserProfileDTO.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/invitation/{user_id}")
    public ResponseEntity<?> findEventsByUserIdWhichUserIsInvited(@PathVariable(value = "user_id") String userId) {
        log.info("[CONTROLLER] start endpoint findEventsByUserIdWhichUserIsInvited with param: {}", userId);
        List<EventInUserProfileDTO> invitedEvents = eventService.findEventsByUserIdWhichUserIsInvited(userId);

        log.info("[CONTROLLER] end endpoint findEventsByUserIdWhichUserIsInvited with response: {}", invitedEvents);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invitedEvents);
    }

    @Operation(
            description = "Screen: Карточка приглашения. Confirm invitation to event",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @PatchMapping("/invitation/confirm/{event_id}/{user_id}")
    public ResponseEntity<?> confirmInvitationToEvent(
            @PathVariable(value = "event_id") Long eventId,
            @PathVariable(value = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint confirmInvitationToEvent with event_id: {} and user_id {}", eventId, userId);

        eventService.confirmInvitationToEvent(eventId, userId);

        log.info("[CONTROLLER] end endpoint confirmInvitationToEvent");
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(
            description = "Screen: Профиль USER. Get events which user is participant",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = EventInUserProfileDTO.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/participant/{user_id}")
    public ResponseEntity<?> findEventsByUserIdWhichUserIsParticipant(@PathVariable(value = "user_id") String userId) {
        log.info("[CONTROLLER] start endpoint findEventsByUserIdWhichUserIsParticipant with param: {}", userId);

        List<EventInUserProfileDTO> participantsEvents = eventService.findEventsByUserIdWhichUserIsParticipant(userId);

        log.info(
                "[CONTROLLER] end endpoint findEventsByUserIdWhichUserIsParticipant with response: {}",
                participantsEvents
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(participantsEvents);
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

    @Operation(
            description = "Screen: Наполнение события. Add event avatar to tmp folder.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = TempFileDTO.class)))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PostMapping("/avatars")
    public ResponseEntity<?> addEventAvatarToTempDirectory(
            @RequestBody ImageDTO imageDTO
    ) {
        String uniqueTempFileName = storageService.saveFileToTmpDir(imageDTO.getContentType(), imageDTO.getImage());
        return ResponseEntity
                .status(HttpStatus.CREATED)
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

    @Operation(
            description = "EventSearch screen. Get event by filter.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = SearchEventDTO.class)))}
    )
    @GetMapping("/search")
    @SecurityRequirement(name = "BearerJWT")
    public ResponseEntity<?> getAllEventByFilter(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            EventFilter eventFilter,
            HttpServletRequest token
    ) {
        log.info("[CONTROLLER] start endpoint getAllEventByFilter");
        log.debug("With data: {}", eventFilter);

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Long currentUserId = null;
        if (token.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            currentUserId = jwtProvider.getUserIdFromToken(token.getHeader(HttpHeaders.AUTHORIZATION));
        }

        PageResponse<SearchEventDTO> response = PageResponse.of(
                eventService.getEventsByFilter(eventFilter, currentUserId, pageable));

        log.info("[CONTROLLER] end endpoint getAllEventByFilter");
        log.debug("With response: {}", response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
