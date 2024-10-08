package com.covenant.tribe.controller;

import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.dto.PageResponse;
import com.covenant.tribe.dto.ResponseErrorDTO;
import com.covenant.tribe.dto.event.*;
import com.covenant.tribe.dto.storage.TempFileDTO;
import com.covenant.tribe.dto.user.UserFavoriteEventDTO;
import com.covenant.tribe.security.JwtProvider;
import com.covenant.tribe.service.facade.EventFacade;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.PhotoStorageService;
import com.covenant.tribe.service.facade.EventSearchFacade;
import com.covenant.tribe.util.mapper.EventMapper;
import com.covenant.tribe.util.querydsl.EventFilter;
import com.covenant.tribe.util.security.TokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    EventMapper eventMapper;
    EventFacade eventFacade;
    EventSearchFacade eventSearchFacade;

    JwtProvider jwtProvider;

    @Operation(
            description = "Категория: создание Евента. Экран: создание Евента. Кнопка: создать евент.  Действие: Создание нового события.",
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
    @PreAuthorize("#requestTemplateForCreatingEventDTO.getOrganizerId().toString().equals(authentication.getName())")
    public ResponseEntity<?> createEvent(
            @Valid @RequestBody RequestTemplateForCreatingEventDTO requestTemplateForCreatingEventDTO
    ) {
        log.info("[CONTROLLER] start endpoint createEvent with RequestBody: {}", requestTemplateForCreatingEventDTO);

        DetailedEventInSearchDTO response = eventFacade.handleNewEvent(requestTemplateForCreatingEventDTO);
        ZonedDateTime now = ZonedDateTime.now();
        ZoneId zone = now.getZone();

        log.info("[CONTROLLER] end endpoint createEvent");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Редактировать удалить. Кнопка: Удалить событие. " +
                    "Действие: Удаление мероприятия организатором",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#organizerId.toString().equals(authentication.getName())")
    @DeleteMapping("/delete/{organizer_id}/{event_id}")
    public ResponseEntity<?> deleteEvent(
            @PathVariable("organizer_id") Long organizerId,
            @PathVariable("event_id") Long eventId
    ) {
        log.info("[CONTROLLER] start endpoint deleteEvent with param: {}, {}", organizerId, eventId);

        eventService.deleteEvent(organizerId, eventId);

        log.info("[CONTROLLER] end endpoint deleteEvent");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @Operation(
            description = "Категория: Splash/Фид/Cards, Избранное, Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    "Экран: Везде, где необходимо получить детальную информацию о мероприятии, напр. CardBig." +
                    "Действие: Получение детальной информации о событии, например для карточки события",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = DetailedEventInSearchDTO.class)))})
    @GetMapping("/{event_id}")
    public ResponseEntity<?> getEventById(
            @PathVariable("event_id") String eventId,
            HttpServletRequest token
    ) {
        log.info("[CONTROLLER] start endpoint getEventById with param: {}", eventId);

        Long currentUserId = null;
        if (token.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            currentUserId = jwtProvider.getUserIdFromToken(token.getHeader(HttpHeaders.AUTHORIZATION));
        }

        DetailedEventInSearchDTO responseEvent = eventService.getDetailedEventById(
                Long.parseLong(eventId), currentUserId
        );

        log.info("[CONTROLLER] end endpoint getEventById");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseEvent);
    }

    @Operation(
            description = "Категория: нет. Экран: Веб админ панель. Действие: Получение всех мероприятий, " +
                    "которые необходимо проверить перед публикацией",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = EventVerificationDTO.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("hasRole('ADMIN')")
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
            description = "Категория: нет Экран: Веб админ панель. Действие: изменение статуса" +
                    " мероприятия на 'Опубликовано'. ",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("hasRole('ADMIN')")
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
            description = "Категория: нет Экран: Веб админ панель. Действие: изменение статуса" +
                    " мероприятия на 'Отправлен на доработку'.",
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
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    "Экран: Профиль ADMIN. Кнопка: Мои события. Действие: Получение всех событий, " +
                    "в которых пользователь выступает в роли организатора",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = EventInUserProfileDTO.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @GetMapping("/organisation/{organizer_id}")
    public ResponseEntity<?> findEventsByOrganizerId(
            @PathVariable(value = "organizer_id") String organizerId,
            Authentication authentication
    ) {
        log.info("[CONTROLLER] start endpoint findEventsByOrganizerId with param: {}", organizerId);
        AbstractAuthenticationToken token = (AbstractAuthenticationToken) authentication;
        Long requestUserId = Long.valueOf(authentication.getName());
        List<EventInUserProfileDTO> organizersEvents = eventService
                .findEventsByOrganizerId(organizerId, requestUserId);

        log.info("[CONTROLLER] end endpoint findEventsByOrganizerId");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(organizersEvents);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    "Экран: Любой, где есть кнопка Приглашения. Кнопка: Приглашения. Действие: Получение всех событий, " +
                    "на которые приглашен пользователь",
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

        log.info("[CONTROLLER] end endpoint findEventsByUserIdWhichUserIsInvited");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(invitedEvents);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    "Экран: Экран приглашений, Карточка приглашения. Кнопка: Посетить. Действие: Подтверждение участия " +
                    "в мероприятии, после получения приглашения.",
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
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +  /* TODO Изменить описание, когда будет создан экран для приглашения пользователя */
                    "Экран: Экран - пока неизвестно. Кнопка: неизвестно. Действие: Отправка приглашения пользователю " +
                    "для участия в мероприятии.",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PatchMapping("/invitation/invite/{event_id}/{user_id}")
    public ResponseEntity<?> inviteUserToEvent(
            @PathVariable(value = "event_id") Long eventId,
            @PathVariable(value = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint inviteUserToEvent with event_id: {} and user_id {}", eventId, userId);

        Long organizerId = TokenUtil.getUserIdFromToken(SecurityContextHolder.getContext());
        Long invitedUserId = Long.valueOf(userId);

        eventService.inviteUserToEvent(eventId, invitedUserId, organizerId);

        log.info("[CONTROLLER] end endpoint inviteUserToEvent");
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }



    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    "Экран: Карточка приглашения. Кнопка: Не пойду. Действие: Отказ от участия " +
                    "в мероприятии, после получения приглашения.",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @PatchMapping("/invitation/decline/{event_id}/{user_id}")
    public ResponseEntity<?> declineInvitationToEvent(
            @PathVariable(value = "event_id") Long eventId,
            @PathVariable(value = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint declineInvitationToEvent with event_id: {} and user_id {}", eventId, userId);

        eventService.declineInvitationToEvent(eventId, userId);

        log.info("[CONTROLLER] end endpoint declineInvitationToEvent");

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    "Экран: ПрофильUSER. Кнопка: События. Действие: Получение всех событий, в которых пользователь " +
                    "принимает участие",
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

        log.info("[CONTROLLER] end endpoint findEventsByUserIdWhichUserIsParticipant");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(participantsEvents);
    }

    @Operation(
            description = "Категория: нет. Экран: нет. Действие: Позволяет организатору подтвердить все заявки " +
                    "пользователей на посещение приватного мероприятия.",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#organizerId.equals(authentication.getName())")
    @PatchMapping("/organizer/participation/confirm/{event_id}/{organizer_id}")
    public ResponseEntity<?> addUserToEventAsParticipant(
            @PathVariable("event_id") Long eventId,
            @PathVariable("organizer_id") String organizerId
    ) {
        log.info(
                "[CONTROLLER] start endpoint addUserToEventAsParticipant with eventId: {} and organizerId: {}"
                , eventId, organizerId
        );
        eventService.addUsersToPrivateEventAsParticipants(eventId, Long.valueOf(organizerId));
        log.info("[CONTROLLER] end endpoint addUserToEventAsParticipant");
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @Operation(
            description = "Категория: нет. Экран: нет. Действие: Позволяет организатору подтвердить одну заявку " +
                    "пользователя на посещение приватного мероприятия.",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#organizerId.equals(authentication.getName())")
    @PatchMapping("/organizer/participation/confirm/{event_id}/{organizer_id}/{user_id}")
    public ResponseEntity<?> addUserToEventAsParticipant(
            @PathVariable("event_id") Long eventId,
            @PathVariable("organizer_id") String organizerId,
            @PathVariable("user_id") Long userId
    ) {
        log.info(
                "[CONTROLLER] start endpoint addUserToEventAsParticipant with eventId: {} and organizerId: {} and userId: {}"
                , eventId, organizerId, userId
        );
        eventService.addUserToPrivateEventAsParticipant(eventId, Long.valueOf(organizerId), userId);
        log.info("[CONTROLLER] end endpoint addUserToEventAsParticipant");
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @Operation(
            description = "Категория: Splash/Фид/Cards, Избранное, Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    "Экран: Экран карточки. Кнопка: Отменить. Действие: Отзыв запроса на " +
                    "посещение приватного мероприятия",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @PatchMapping("/participant/request/private/decline/{event_id}/{user_id}")
    public ResponseEntity<?> withdrawalRequestToParticipateInPrivateEvent(
            @PathVariable(value = "event_id") Long eventId,
            @PathVariable(value = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint sendToOrganizerARequestToParticipationInPrivateEvent");
        eventService.withdrawalRequestToParticipateInPrivateEvent(eventId, Long.parseLong(userId));

        log.info("[CONTROLLER] end endpoint sendToOrganizerARequestToParticipationInPrivateEvent");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @Operation(
            description = "Категория: Splash/Фид/Cards, Избранное, Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    "Экран: Экран карточки. Кнопка: Хочу пойти. Действие: Отправка запроса организатору для " +
                    "посещения приватного мероприятия",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @PatchMapping("/participant/request/private/{event_id}/{user_id}")
    public ResponseEntity<?> sendToOrganizerARequestToParticipationInPrivateEvent(
            @PathVariable(value = "event_id") Long eventId,
            @PathVariable(value = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint sendToOrganizerARequestToParticipationInPrivateEvent" +
                " with event_id: {} and user_id {}", eventId, userId);
        eventService.sendToOrganizerRequestToParticipationInPrivateEvent(eventId, userId);

        log.info("[CONTROLLER] end endpoint sendToOrganizerARequestToParticipationInPrivateEvent");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @Operation(
            description = "Категория: Splash/Фид/Cards, Избранное, Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    "Экран: Экран карточки. Кнопка: Хочу пойти. Действие: Позволяет зарегистрироваться на" +
                    " публичное мероприятие в качестве посетителя.",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @PatchMapping("/participant/request/public/{event_id}/{user_id}")
    public ResponseEntity<?> sendRequestToParticipationInPublicEvent(
            @PathVariable(value = "event_id") Long eventId,
            @PathVariable(value = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint sendRequestToParticipationInPublicEvent" +
                " with event_id: {} and user_id {}", eventId, userId);
        eventService.sendRequestToParticipationInPublicEvent(eventId, userId);

        log.info("[CONTROLLER] end endpoint sendRequestToParticipationInPublicEvent");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @Operation(
            description = "Категория: нет. Экран: нет. Действие: Позволяет отказаться от посещения мероприятия.",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @PatchMapping("/participant/decline/{event_id}/{user_id}")
    public ResponseEntity<?> declineToParticipantInEvent(
            @PathVariable(value = "event_id") Long eventId,
            @PathVariable(value = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint declineToParticipantInEvent with event_id: {} and user_id {}", eventId, userId);

        eventService.declineToParticipantInEvent(eventId, userId);

        log.info("[CONTROLLER] end endpoint declineToParticipantInEvent");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }


    @Operation(
            description = "Категория: Создание Евента. Экран: Наполнение события. Кнопка: Add photo. Действие: " +
                    "Позволяет отправить на сервер для сохранения фото/аватар мероприятия",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = TempFileDTO.class)))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PostMapping("/avatars")
    public ResponseEntity<?> addEventAvatarToTempDirectory(
            @RequestBody ImageDto imageDTO
    ) {
        log.info("[CONTROLLER] start endpoint addEventAvatarToTempDirectory");
        String uniqueTempFileName = storageService.saveFileToTmpDir(imageDTO.getContentType(), imageDTO.getImage());
        log.info("[CONTROLLER] end endpoint addEventAvatarToTempDirectory");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TempFileDTO(uniqueTempFileName));
    }

    @Operation(
            description = "Категория: Splash/Фид/Cards, Избранное, Создание Евента, Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    ". Экран: Любой, на котором нужно получить фото/аватар мероприятия. Действие: " +
                    "Позволяет получить фото/аватар мероприятия",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = Byte.class))))}
    )
    @GetMapping("/avatars/{added_date}/{avatar_file_name}")
    public ResponseEntity<?> getEventAvatar(
            @PathVariable(value = "added_date") String addedDate,
            @PathVariable(value = "avatar_file_name") String avatarFileName
    ) throws FileNotFoundException {
        log.info("[CONTROLLER] start endpoint getEventAvatar");
        ImageDto imageDTO = storageService.getEventAvatar(addedDate + File.separator + avatarFileName);
        log.info("[CONTROLLER] end endpoint getEventAvatar");
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(imageDTO.getContentType()))
                .body(imageDTO.getImage());
    }

    @Operation(
            description = "Категория: Splash/Фид/Cards/ Экран: Фильтр, главный экран приложения. Действие: " +
                    "Позволяет получить мероприятия, отфильтрованные в соответствии с заданными параметрами",
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
            @RequestParam(value = "size", defaultValue = "100") Integer size,
            EventFilter eventFilter,
            HttpServletRequest token
    ) throws JsonProcessingException {

        log.info("[CONTROLLER] start endpoint getAllEventByFilter");

        Long currentUserId = null;
        if (token.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            currentUserId = jwtProvider.getUserIdFromToken(token.getHeader(HttpHeaders.AUTHORIZATION));
        }

        PageResponse<SearchEventDTO> response = PageResponse.of(
                eventSearchFacade.getEventsByFilter(eventFilter, currentUserId, page, size)
        );

        log.info("[CONTROLLER] end endpoint getAllEventByFilter");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(
            description = "Категория: Фильтр  Экран: Фильтр расширенный. Действие: доставка количества " +
                    "отфильтрованных событий, для отображения на кнопке - Показать",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = FilteredEventQuantityDto.class)))}
    )
    @GetMapping("/search/quantity")
    public ResponseEntity<?> getFilteredEventQuantity(EventFilter eventFilter) {
        log.info("[CONTROLLER] start endpoint getFilteredEventQuantity");

        FilteredEventQuantityDto filteredEventQuantity = eventService.getFilteredEventQuantity(eventFilter);

        log.info("[CONTROLLER] end endpoint getFilteredEventQuantity");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(filteredEventQuantity);
    }

    @Operation(
            description = "Категория: Splash/Фид/Cards/, Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/" +
                    " Экран: Фильтр, главный экран приложения, Экран карточки. Кнопка: сердце. Действие: " +
                    "Добавляет выбранное событие в избранное пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userFavoriteEventDTO.getUserId().equals(authentication.getName())")
    @PostMapping("/favorite")
    public ResponseEntity<?> saveEventToFavorites(@RequestBody UserFavoriteEventDTO userFavoriteEventDTO) {
        log.info("[CONTROLLER] start endpoint saveEventToFavorites with param: {}", userFavoriteEventDTO);

        eventService.saveEventToFavorite(
                Long.parseLong(userFavoriteEventDTO.getUserId()), userFavoriteEventDTO.getEventId()
        );

        log.info("[CONTROLLER] end endpoint findUserByUsernameForSendInvite");
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @Operation(
            description = "Категория: Splash/Фид/Cards/, Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/, Избранное" +
                    " Экран: Фильтр, главный экран приложения, Экран карточки. Кнопка: сердце. мусорный бачок. Действие: " +
                    "Удаляет выбранное событие в избранное пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @DeleteMapping("/favorite/{user_id}/{event_id}")
    public ResponseEntity<?> deleteEventFromFavorites(
            @PathVariable(value = "user_id") String userId,
            @PathVariable(value = "event_id") Long eventId
    ) {
        log.info("[CONTROLLER] start endpoint deleteEventFromFavorites");
        eventService.removeEventFromFavorite(Long.parseLong(userId), eventId);
        log.info("[CONTROLLER] end endpoint deleteEventFromFavorites");
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(
            description = "Категория: Избранное. Экран: Избранное. Действие: " +
                    "Получение всех избранных мероприятий пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = EventInFavoriteDTO.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/favorite/{user_id}")
    @Transactional
    public ResponseEntity<?> getAllFavoritesByUserId(
            @PathVariable(value = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint getAllFavoritesByUserId with param: {}", userId);

        List<EventInFavoriteDTO> userFavorites = eventService.getAllFavoritesByUserId(Long.parseLong(userId)).stream()
                .map(eventMapper::mapToEventInFavoriteDTO)
                .toList();

        log.info("[CONTROLLER] end endpoint getAllFavoritesByUserId");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userFavorites);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Наполнение события. Действие: " +
                    "Получение информации о событии для обновления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = EventDto.class)))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#organizerId.equals(authentication.getName())")
    @GetMapping("/update/{event_id}/{organizer_id}")
    public ResponseEntity<?> getEventForUpdating(
            @PathVariable(value = "event_id") Long eventId,
            @PathVariable(value = "organizer_id") String organizerId
    ) {
        log.info("[CONTROLLER] start endpoint getEventForUpdating with param: {}, {}", eventId, organizerId);

        EventDto event = eventService.getEventForUpdating(eventId, Long.valueOf(organizerId));

        log.info("[CONTROLLER] end endpoint getEventForUpdating");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(event);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Наполнение события. Действие: " +
                    "Обновление информации о событии. Кнопка: Сохранить изменения",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = DetailedEventInSearchDTO.class)))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#updateEventDto.organizerId.equals(authentication.getName())")
    @PatchMapping("/update")
    public ResponseEntity<?> updateEvent(
            @RequestBody @Valid UpdateEventDto updateEventDto
    ) throws IOException {
        log.info("[CONTROLLER] start endpoint updateEvent with updateEventDto: {}", updateEventDto);

        DetailedEventInSearchDTO detailedEventDto = eventService.updateEvent(updateEventDto);

        log.info("[CONTROLLER] end endpoint updateEvent");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(detailedEventDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/parts_of_day")
    @ResponseStatus(HttpStatus.OK)
    public void updateEventsPartsOfDay() {
        eventService.updatePartsOfDay();
    }

}
