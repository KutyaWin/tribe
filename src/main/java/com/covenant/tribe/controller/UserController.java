package com.covenant.tribe.controller;

import com.covenant.tribe.dto.event.EventInFavoriteDTO;
import com.covenant.tribe.dto.user.UserForSignInUpDTO;
import com.covenant.tribe.dto.user.UserFavoriteEventDTO;
import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import com.covenant.tribe.service.UserService;
import com.covenant.tribe.util.mapper.EventMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    UserService userService;
    EventMapper eventMapper;

    @Operation(
            tags = "User",
            description = "Android Small 39 screen. Get a User by username.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = UserToSendInvitationDTO.class)))})
    @GetMapping
    public ResponseEntity<?> findUserByUsernameForSendInvite(@RequestParam(value = "username") String username) {
        log.info("[CONTROLLER] start endpoint findUserByUsernameForSendInvite with param: {}", username);

        UserToSendInvitationDTO responseUser =
                userService.findUserByUsernameForSendInvite(username);

        log.info("[CONTROLLER] end endpoint findUserByUsernameForSendInvite with response: {}", responseUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseUser);
    }

    @PostMapping("/favorite")
    public ResponseEntity<?> saveEventToFavorites(@RequestBody UserFavoriteEventDTO userFavoriteEventDTO) {
        log.info("[CONTROLLER] start endpoint saveEventToFavorites with param: {}", userFavoriteEventDTO);

        userService.saveEventToFavorite(userFavoriteEventDTO.getUserId(), userFavoriteEventDTO.getEventId());

        log.info("[CONTROLLER] end endpoint findUserByUsernameForSendInvite");
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

    @Operation(
            tags = "User",
            description = "Favorite screen. Get all favorite events by user_id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = EventInFavoriteDTO.class)))})
    @GetMapping("/favorite/{user_id}")
    @Transactional
    public ResponseEntity<?> getAllFavoritesByUserId(
            @PathVariable(value = "user_id") Long userId
    ) {
        log.info("[CONTROLLER] start endpoint getAllFavoritesByUserId with param: {}", userId);

        List<EventInFavoriteDTO> userFavorites = userService.getAllFavoritesByUserId(userId).stream()
                .map(eventMapper::mapToEventInFavoriteDTO)
                .toList();

        log.info("[CONTROLLER] end endpoint getAllFavoritesByUserId with response: {}", userFavorites);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userFavorites);
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

    @GetMapping("/username/check/{username}")
    public ResponseEntity<?> isUsernameExistCheck(
            @PathVariable String username
    ) {
        boolean isUsernameExist = userService.isUsernameExist(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(isUsernameExist);
    }

}
