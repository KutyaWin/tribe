package com.covenant.tribe.controller;

import com.covenant.tribe.dto.user.*;
import com.covenant.tribe.dto.event.EventInFavoriteDTO;
import com.covenant.tribe.service.UserService;
import com.covenant.tribe.util.mapper.EventMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "User")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    UserService userService;
    EventMapper eventMapper;

    @Operation(
            description = "Android Small 39 screen. Get a User by username.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserToSendInvitationDTO.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @GetMapping("/username/partial/{username}")
    public ResponseEntity<?> findUserByUsernameForSendInvite(
            @PathVariable(value = "username") String partialUsername,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size
    ) {
        log.info("[CONTROLLER] start endpoint findUserByUsernameForSendInvite with param: {}", partialUsername);

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<UserToSendInvitationDTO> responseUser =
                userService.findUsersByContainsStringInUsernameForSendInvite(partialUsername, pageable);

        log.info("[CONTROLLER] end endpoint findUserByUsernameForSendInvite with response: {}", responseUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseUser);
    }

    @Operation(
            description = "Screen подписчики. Get all user's subscribers by partial username.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserSubscriberDto.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/subscriber/partial/{subscriber_username}/{user_id}")
    public ResponseEntity<?> findAllSubscribersByUsername(
            @PathVariable(value = "subscriber_username") String subscriberUsername,
            @PathVariable(value = "user_id") String userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size
    ) {
        log.info("[CONTROLLER] start endpoint findAllSubscribersByUsername with param: {}", subscriberUsername);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserSubscriberDto> responseUser = userService.findAllSubscribersByUsername(subscriberUsername, Long.parseLong(userId), pageable);

        log.info("[CONTROLLER] end endpoint findAllSubscribersByUsername with response: {}", responseUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseUser);
    }

    @Operation(
            description = "Screen:  Подписаться. Get all users that the user is not subscribed to",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserUnSubscriberDto.class))))},
            security = @SecurityRequirement(name = "BearerJWT"))
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/unsubscriber/{user_id}")
    public ResponseEntity<?> findAllUnSubscribers(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size,
            @PathVariable(name = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint findAllUnSubscribers with param: {}", userId);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserUnSubscriberDto> unsubscribers = userService.findAllUnSubscribers(Long.parseLong(userId), pageable);

        log.info("[CONTROLLER] end endpoint findAllUnSubscribers with response: {}", unsubscribers);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(unsubscribers);
    }

    @Operation(
            description = "Screen:  Подписаться. Get all users that the user is not subscribed to by partial username",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserUnSubscriberDto.class))))},
            security = @SecurityRequirement(name = "BearerJWT"))
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/unsubscriber/partial/{unsubscriber_username}/{user_id}")
    public ResponseEntity<?> findAllUnSubscribersByUsername(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size,
            @PathVariable(name = "unsubscriber_username") String unsubscriberUsername,
            @PathVariable(name = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint findAllUnSubscribersByUsername with param: {}", unsubscriberUsername);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserUnSubscriberDto> unsubscribers = userService.findAllUnSubscribersByUsername(
                unsubscriberUsername, Long.parseLong(userId), pageable
        );

        log.info("[CONTROLLER] end endpoint findAllUnSubscribersByUsername with response: {}", unsubscribers);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(unsubscribers);

    }


    @Operation(
            description = "Screen подписчики. Get all user's subscribers.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserSubscriberDto.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/subscriber/{user_id}")
    public ResponseEntity<?> findAllSubscribersByUsername(
            @PathVariable(value = "user_id") String userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size
    ) {
        log.info("[CONTROLLER] start endpoint findAllSubscribers with response: {}", userId);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserSubscriberDto> responseUser = userService.findAllSubscribers(Long.parseLong(userId), pageable);

        log.info("[CONTROLLER] end endpoint findAllSubscribers with response: {}", responseUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseUser);
    }

    @Operation(
            description = "Screen: Подписчики. Subscribe to user.",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#subscriptionDto.followerUserId.toString().equals(authentication.getName())")
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeToUser(
            @RequestBody SubscriptionDto subscriptionDto
    ) {
        log.info("[CONTROLLER] start endpoint subscribeToUser with param: {}", subscriptionDto);

        userService.subscribeToUser(subscriptionDto);

        log.info("[CONTROLLER] end endpoint subscribeToUser");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @Operation(
            description = "Screen: Подписки. Unsubscribe from user.",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#subscriptionDto.followerUserId.toString().equals(authentication.getName())")
    @PutMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribeFromUser(
            @RequestBody SubscriptionDto subscriptionDto
    ) {
        log.info("[CONTROLLER] start endpoint unsubscribeFromUser with param: {}", subscriptionDto);

        userService.unsubscribeFromUser(subscriptionDto);

        log.info("[CONTROLLER] end endpoint unsubscribeFromUser");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
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
