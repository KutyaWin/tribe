package com.covenant.tribe.controller;

import com.covenant.tribe.dto.ImageDTO;
import com.covenant.tribe.dto.event.EventDTO;
import com.covenant.tribe.dto.storage.TempFileDTO;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.PhotoStorageService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

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
            @RequestBody ImageDTO imageDTO
    ) {
        String uniqueTempFileName = storageService.saveFileToTmpDir(imageDTO.getContentType(), imageDTO.getImage());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new TempFileDTO(uniqueTempFileName));
    }

    @GetMapping("/avatar/{added_date}/{avatar_file_name}")
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


}
