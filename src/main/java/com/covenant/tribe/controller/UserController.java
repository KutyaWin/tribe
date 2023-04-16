package com.covenant.tribe.controller;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.EventDTO;
import com.covenant.tribe.dto.user.UserDTO;
import com.covenant.tribe.dto.user.UserFavoriteEventDTO;
import com.covenant.tribe.mapper.EventMapper;
import com.covenant.tribe.mapper.UserMapper;
import com.covenant.tribe.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    UserService userService;
    EventMapper eventMapper;

    @PostMapping
    @Operation(
            tags = "User",
            description = "Create a new user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = UserDTO.class)))})
    public ResponseEntity<?> saveNewUser(@Valid @RequestBody UserDTO userDTO) {
        log.debug("[CONTROLLER] start endpoint saveNewUser with param: {}", userDTO);
        User savedUser = userService.saveUser(userDTO);
        log.debug("[CONTROLLER] end endpoint saveNewUser with response: {}", savedUser);
        return new ResponseEntity<>(UserMapper.mapUserToUserDTO(savedUser), HttpStatus.OK);
    }

    @PostMapping("/favorite")
    public ResponseEntity<?> saveEventToFavorites(@RequestBody UserFavoriteEventDTO userFavoriteEventDTO) {
        userService.saveEventToFavorite(userFavoriteEventDTO.getUserId(), userFavoriteEventDTO.getEventId());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @DeleteMapping("/favorite")
    public ResponseEntity<?> deleteEventFromFavorites(@RequestBody UserFavoriteEventDTO userFavoriteEventDTO) {
        userService.removeEventFromFavorite(userFavoriteEventDTO.getUserId(), userFavoriteEventDTO.getEventId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/favorite/{user_id}")
    @Transactional
    public ResponseEntity<?> getAllFavoritesByUserId(
            @PathVariable(value = "user_id") Long userId
    ) {
        List<Event> userFavorites = userService.getAllFavoritesByUserId(userId);
        List<EventDTO> eventDTOs = userFavorites
                .stream().map(event -> eventMapper.mapEventToEventDTO(event, userId)).toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDTOs);
    }

    @GetMapping("/email/check/{email}")
    public ResponseEntity<?> isEmailExistCheck(
            @PathVariable String email
    ) {
        boolean isEmailExist = userService.isEmailExist(email);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(isEmailExist);
    }

}
