package com.covenant.tribe.controller;

import com.covenant.tribe.dto.user.ProfessionDto;
import com.covenant.tribe.service.ProfessionService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@Tag(name = "Profession")
@RequestMapping("api/v1/profession")
public class ProfessionController {

    ProfessionService professionService;

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Настройки профиля" +
                    " Действие: Получение всех профессий, для выбора профессий пользователем.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = ProfessionDto.class)))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @GetMapping()
    public ResponseEntity<?> getAllProfessions() {
        log.info("[CONTROLLER] start endpoint getAllProfessions");
        List<ProfessionDto> professionDtoList = professionService.getAllProfessions();
        log.info("[CONTROLLER] end endpoint getAllProfessions");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(professionDtoList);
    }

}
