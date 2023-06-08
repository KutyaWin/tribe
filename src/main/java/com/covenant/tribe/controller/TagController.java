package com.covenant.tribe.controller;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.dto.TagDTO;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.EventTagDTO;
import com.covenant.tribe.service.TagService;
import com.covenant.tribe.util.mapper.EventTagMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("api/v1/tags")
public class TagController {

    TagService tagService;
    EventTagMapper eventTagMapper;
    @Operation(
            tags = "Tag",
            description = "Категория: Создание Евента. Экран: Наполнение события. Input для выбора тегов" +
                    " Действие: Получение всех тегов соответствующих типу выбранного ивента",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = EventTagDTO.class))))}
    )
    @Transactional
    @GetMapping("/{event_type_id}")
    public ResponseEntity<?> getAllTagsByEventTypeId(
            @PathVariable("event_type_id") Long eventTypeId
    ) {
        Set<Tag> eventTags = tagService.getAllTagsByEventTypeId(eventTypeId);
        List<EventTagDTO> eventTagDTOs = eventTags
                .stream()
                .map(eventTag -> eventTagMapper
                        .mapEventTagToEventTagDTO(eventTag))
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventTagDTOs);
    }

}
