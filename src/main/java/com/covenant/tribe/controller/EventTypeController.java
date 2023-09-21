package com.covenant.tribe.controller;

import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.dto.event.EventTypeInfoDto;
import com.covenant.tribe.service.EventTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Event type")
@RequestMapping("api/v1")
public class EventTypeController {

    EventTypeService eventTypeService;

    @Operation(
            description = "Категория: Создание Евента. Экран: Создание события." +
                    " Действие: Получение всех анимаций, для выбора типа события, при создании мероприятия.",
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
        log.info("[CONTROLLER] start endpoint getRectangleEventTypes");
        List<EventTypeDTO> eventTypeDTOs = eventTypeService.getAllRectangleEventTypes(isDark);
        log.info("[CONTROLLER] end endpoint getRectangleEventTypes");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventTypeDTOs);
    }

    @Operation(
            description = "Категория: Splash/Фид/Cards. Экран: Like me." +
                    " Действие: Получение списка анимаций, которые можно использовать для выбора типов событий, " +
                    "интересных новому пользователю.",
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
        log.info("[CONTROLLER] start endpoint getCircleEventTypes");
        List<EventTypeDTO> eventTypeDTOs = eventTypeService.getAllCircleEventTypes(isDark);
        log.info("[CONTROLLER] end endpoint getCircleEventTypes");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventTypeDTOs);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Настройки профиля." +
                    " Действие: Получение всех возможных типов событий с именами и id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                array = @ArraySchema(
                                    schema = @Schema(implementation = EventTypeInfoDto.class)
                                )
                            )
                    )
            }
    )
    @GetMapping("/event/type/info")
    public ResponseEntity<?> getEventTypeInfo() {
        log.info("[CONTROLLER] start endpoint getEventTypeInfo");

        List<EventTypeInfoDto> eventTypeInfoDtoList = eventTypeService.getEventTypeInfo();

        log.info("[CONTROLLER] end endpoint getEventTypeInfo");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventTypeInfoDtoList);
    }
}
