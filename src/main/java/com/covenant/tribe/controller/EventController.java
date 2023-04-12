package com.covenant.tribe.controller;

import com.covenant.tribe.dto.event.EventDTO;
import com.covenant.tribe.service.EventService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/event")
public class EventController {

    EventService eventService;

    @GetMapping("/{event_id}")
    public ResponseEntity<?> getEventById(
            @RequestParam(value = "user-id", required = false) Long userId,
            @PathVariable("event_id") String eventId
    ) {
        EventDTO eventDTO = eventService.getEventById(Long.parseLong(eventId), userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDTO);
    }

}
