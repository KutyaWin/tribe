package com.covenant.tribe.controller;

import com.covenant.tribe.service.ExternalEventHandlerFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "External Event")
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/events/external")
public class ExternalEventController {

    ExternalEventHandlerFacade externalEventHandlerFacade;

    @Operation(
            description = "Действие: Получение событий из сервиса Куда Го",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/kudago/{min_publication_date}")
    public ResponseEntity<?> getKudaGoEvents(
            @PathVariable(name = "min_publication_date"
            ) String minPublicationDate) {
        log.info("[CONTROLLER] start endpoint getKudaGoEvents");

        externalEventHandlerFacade.handleNewEvents(
                minPublicationDate
        );

        log.info("[CONTROLLER] end endpoint getKudaGoEvents");

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
