package com.covenant.tribe.controller;

import com.covenant.tribe.dto.event.EventDTO;
import com.covenant.tribe.dto.storage.TempFileDTO;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.PhotoStorageService;
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
    PhotoStorageService storageService;

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

    @PostMapping("/{event_id}/{user_id}")
    public ResponseEntity<?> addUserToEvent(
            @PathVariable("event_id") Long eventId,
            @PathVariable("user_id") Long userId
    ) {
        eventService.addUserToEvent(eventId, userId);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> addEventAvatarToTempDirectory(
            @RequestHeader("Content-Type") String contentType, @RequestBody byte[] avatar
    ) {
        String uniqueTempFileName = storageService.saveFileToTmpDir(contentType, avatar);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new TempFileDTO(uniqueTempFileName));
    }

}
