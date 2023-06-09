package com.covenant.tribe.controller;

import com.covenant.tribe.dto.ImageDto;
import com.covenant.tribe.dto.storage.TempFileDTO;
import com.covenant.tribe.dto.user.*;
import com.covenant.tribe.service.PhotoStorageService;
import com.covenant.tribe.service.UserService;
import com.covenant.tribe.util.mapper.EventMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@Slf4j
@Tag(name = "User")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    UserService userService;
    EventMapper eventMapper;
    PhotoStorageService storageService;

    @Operation(
            description = "Категория: создание Евента. Экран: Приглашение участников. Поле для поиска." +
                    " Действие: Получение всех пользователей, у которых username совпадает с введенным в поле поиска." +
                    " (Можно передавать не username целиком, а первые несколько символов.)",
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
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Подписчики. Поле для поиска." +
                    " Действие: Получение всех подписчиков пользователя, у которых username совпадает с введенным в поле поиска." +
                    " (Можно передавать не username целиком, а первые несколько символов.)",
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
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Подписаться. Поле для поиска." +
                    " Действие: Получение всех пользователей, на которых не подписан текущий пользователь",
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
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Подписаться. Поле для поиска." +
                    " Действие: Получение всех пользователей, на которых не подписан текущий пользователь, " +
                    "у которых username совпадает с введенным в поле поиска." +
                    " (Можно передавать не username целиком, а первые несколько символов.)",
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
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Подписчики. Поле для поиска." +
                    " Действие: Получение всех подписчиков, текущего пользователя у которых username совпадает" +
                    " с введенным в поле поиска. (Можно передавать не username целиком, а первые несколько символов.)",
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
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Подписчики. Кнопка: подписаться." +
                    " Действие: подписаться на пользователя.",
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
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Подписки. Кнопка: мусорный ящик." +
                    " Действие: отписаться от пользователя.",
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

    @Operation(
            description = "Категория: Вход/Регистрация. Экран: Любой, где необходима проверка." +
                    " Действие: Проверка существует ли пользователь с заданным email.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    )
            }
    )
    @GetMapping("/email/check/{email}")
    public ResponseEntity<?> isEmailExistCheck(
            @PathVariable String email
    ) {
        boolean isEmailExist = userService.isEmailExist(email);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(isEmailExist);
    }

    @Operation(
            description = "Категория: Вход/Регистрация. Экран: Любой, где необходима проверка." +
                    " Действие: Проверка существует ли пользователь с заданным username.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    )
            }
    )
    @GetMapping("/username/check/{username}")
    public ResponseEntity<?> isUsernameExistCheck(
            @PathVariable String username
    ) {
        boolean isUsernameExist = userService.isUsernameExist(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(isUsernameExist);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Настройки внутри." +
                    " Действие: Получение всех данных о пользователе, которые возможно изменить в профиле пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = UserGetDto.class)
                            )
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(
            @PathVariable String userId
    ) {
        log.info("[CONTROLLER] start endpoint getUserProfile with param: {}", userId);

        UserGetDto userGetDto = userService.getUser(Long.parseLong(userId));

        log.info("[CONTROLLER] end endpoint getUserProfile");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGetDto);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Настройки внутри." +
                    " Действие: Добавление аватара пользователя во временную папку. После обновления данных " +
                    "пользователя, аватар перемещается в постоянное хранилище изображений",
            responses = {
                    @ApiResponse(
                            responseCode = "201"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @PostMapping("/avatar/{user_id}")
    public ResponseEntity<?> uploadAvatarToTempFolder(
            @PathVariable(name = "user_id") String userId,
            @RequestBody ImageDto imageDto
    ) {
        log.info("[CONTROLLER] start endpoint uploadAvatarToTempFolder with param: {}, and contentType: {}",
                userId, imageDto.getContentType());

        String fileName = userService.uploadAvatarToTempFolder(Long.parseLong(userId), imageDto);

        log.info("[CONTROLLER] end endpoint uploadAvatarToTempFolder");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TempFileDTO(fileName));
    }

    @Operation(
            description = "Категория: Любая, где требуется аватар пользователя. Экран: Любой, где требуется аватар пользователя" +
                    " Действие: Получение аватара пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Byte.class)
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @GetMapping("/avatar/{added_date}/{file_name}")
    public ResponseEntity<?> getUserAvatar(
            @PathVariable(name = "file_name") String fileName,
            @PathVariable(name = "added_date") String addedDate
    ) throws FileNotFoundException {
        log.info("[CONTROLLER] start endpoint getAvatar with param: {}", fileName);

        ImageDto imageDto = storageService.getUserAvatar(addedDate + "/" + fileName);

        log.info("[CONTROLLER] end endpoint getAvatar");

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(imageDto.getContentType()))
                .body(imageDto.getImage());
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Настройки внутри." +
                    " Действие: Обновление данных в профиле пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "202"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userProfileUpdateDto.userId.toString().equals(authentication.getName())")
    @PatchMapping()
    public ResponseEntity<?> updateUserProfile(
         @RequestBody @Valid UserProfileUpdateDto userProfileUpdateDto
    ) {
        log.info("[CONTROLLER] start endpoint updateUserProfile with param: {}", userProfileUpdateDto);

        userService.updateUserProfile(userProfileUpdateDto);

        log.info("[CONTROLLER] end endpoint updateUserProfile");

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Профиль ADMIN, Профиль USRER" +
                    " Действие: Получение информации о пользователе.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = ProfileDto.class)
                            )
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/profile/{user_id}")
    public ResponseEntity<?> getUserProfile(
            @PathVariable(name = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint getUserProfile with param: {}", userId);

        ProfileDto profileDto = userService.getProfile(Long.parseLong(userId));

        log.info("[CONTROLLER] end endpoint getUserProfile");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(profileDto);
    }
}
