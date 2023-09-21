package com.covenant.tribe.controller;

import com.covenant.tribe.dto.user.UnknownUserWithInterestsDTO;
import com.covenant.tribe.service.UnknownUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Tag(name = "Unknown user")
@RequestMapping("api/v1/unknown-user")
public class UnknownUserController {

    UnknownUserService unknownUserService;

    @Operation(
            description = "Категория: Splash/Фид/Cards. Экран: Like me. Кнопка: Поехали!" +
                    " Действие: Сохранение интересов неизвестного пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = Long.class)))}
    )
    @PostMapping("/interests")
    public ResponseEntity<?> saveUnknownUserWithInterests(
            @RequestBody UnknownUserWithInterestsDTO unknownUserWithInterests
    ) {
        log.info("[CONTROLLER] start endpoint saveUnknownUserWithInterests");
        Long unknownUserId = unknownUserService.saveNewUnknownUserWithInterests(
                unknownUserWithInterests
        );
        log.info("[CONTROLLER] end endpoint saveUnknownUserWithInterests");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(unknownUserId);
    }

}
