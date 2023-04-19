package com.covenant.tribe.controller;

import com.covenant.tribe.dto.user.*;
import com.covenant.tribe.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    UserService userService;

    @PostMapping
    @Operation(
            tags = "User",
            description = "Create a new user")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody TESTUserForSignUpDTO requestUser) {
        log.info("[CONTROLLER] start endpoint createNewUser with param: {}", requestUser);

        TESTUserForSignUpDTO responseUser = userService.saveTestNewUser(requestUser);

        log.info("[CONTROLLER] end endpoint createNewUser with response: {}", responseUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseUser);
    }

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
        userService.saveEventToFavorite(userFavoriteEventDTO.getUserId(), userFavoriteEventDTO.getEventId());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }
}
