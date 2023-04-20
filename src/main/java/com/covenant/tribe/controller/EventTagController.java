package com.covenant.tribe.controller;

import com.covenant.tribe.domain.event.EventTag;
import com.covenant.tribe.dto.event.EventTagDTO;
import com.covenant.tribe.mapper.EventTagMapper;
import com.covenant.tribe.service.EventTagService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("api/v1/tag")
public class EventTagController {

    EventTagService eventTagService;
    EventTagMapper eventTagMapper;

    @Transactional
    @GetMapping("/{event_type_id}")
    public ResponseEntity<?> getAllTagsByEventTypeId(
            @PathVariable("event_type_id") Long eventTypeId
    ) {
        Set<EventTag> eventTags = eventTagService.getAllTagsByEventTypeId(eventTypeId);
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
