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
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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

        log.info("[CONTROLLER] end endpoint findUserByUsernameForSendInvite}");
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
    @GetMapping("/subscriber/partial/{user_id}")
    public ResponseEntity<?> findAllSubscribersByUsername(
            @RequestParam(value = "subscriber_username") String subscriberUsername,
            @PathVariable(value = "user_id") String userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size
    ) {
        log.info("[CONTROLLER] start endpoint findAllSubscribersByUsername with param: {}", subscriberUsername);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserSubscriberDto> responseUser = userService.findAllSubscribersByUsername(subscriberUsername,
                Long.parseLong(userId), pageable);

        log.info("[CONTROLLER] end endpoint findAllSubscribersByUsername");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseUser);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Подписчики. Поле для поиска." +
                    " Действие: Получение всех подписчиков, текущего пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserSubscriberDto.class))))},
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @GetMapping("/subscriber/{user_id}")
    public ResponseEntity<?> findAllSubscribersById(
            @PathVariable(value = "user_id") String userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size
    ) {
        log.info("[CONTROLLER] start endpoint findAllSubscribers with response: {}", userId);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserSubscriberDto> responseUser = userService.findAllSubscribers(Long.parseLong(userId), pageable);

        log.info("[CONTROLLER] end endpoint findAllSubscribers");

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

        log.info("[CONTROLLER] end endpoint findAllUnSubscribers");
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
    @GetMapping("/unsubscriber/partial/{user_id}")
    public ResponseEntity<?> findAllUnSubscribersByUsername(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size,
            @RequestParam(name = "unsubscriber_username") String unsubscriberUsername,
            @PathVariable(name = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint findAllUnSubscribersByUsername with param: {}", unsubscriberUsername);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserUnSubscriberDto> unsubscribers = userService.findAllUnSubscribersByUsername(
                unsubscriberUsername, Long.parseLong(userId), pageable
        );

        log.info("[CONTROLLER] end endpoint findAllUnSubscribersByUsername");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(unsubscribers);

    }

    @Operation(
            description = """
                        Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Подписки. Поле для поиска.
                        Действие: Получение всех подписок, текущего пользователя.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = UserSubscriberDto.class)
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @GetMapping("/following/{user_id}")
    public ResponseEntity<?> findAllFollowingById(
            @PathVariable(value = "user_id") String userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size
    ) {
        log.info("[CONTROLLER] start endpoint findAllFollowing");

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserSubscriberDto> responseUser = userService.findAllFollowing(Long.parseLong(userId), pageable);

        log.info("[CONTROLLER] end endpoint findAllFollowing");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseUser);

    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Подписчики. Поле для поиска." +
                    " Действие: Получение всех подписок пользователя, у которых username совпадает с введенным в поле поиска." +
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
    @GetMapping("/following/partial/{user_id}")
    public ResponseEntity<?> findAllFollowingsByUsername(
            @PathVariable("user_id") String userId,
            @RequestParam(value = "user_name") String username,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "100") Integer size
    ) {
        log.info("[CONTROLLER] start endpoint findAllFollowingsByUsername");

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserSubscriberDto> responseUser = userService.findAllFollowingsByUsername(
                username, Long.parseLong(userId), pageable
        );

        log.info("[CONTROLLER] end endpoint findAllFollowingsByUsername");

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
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Настройки профиля." +
                    " Действие: Отправка кода подтверждения на email пользователя.",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userEmailDto.getUserId().equals(authentication.getName())")
    @PostMapping("/email/change/code")
    public ResponseEntity<?> sendEmailConfirmationCode(
            @RequestBody @Valid UserEmailDto userEmailDto
    ) {
        log.info("[CONTROLLER] start endpoint sendEmailConfirmationCode with param: {}", userEmailDto);

        userService.sendConfirmationCodeToEmail(userEmailDto);

        log.info("[CONTROLLER] end endpoint sendEmailConfirmationCode");

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Пока нет." +
                    " Действие: Подтверждение кода при изменении email пользователя.",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PatchMapping("/email/change/confirm")
    public ResponseEntity<?> confirmEmailChange(
            @RequestBody @Valid EmailChangeDto emailChangeDto

    ) {
        log.info("[CONTROLLER] start endpoint confirmEmailChange with param: {}", emailChangeDto);

        userService.confirmEmailChange(emailChangeDto);

        log.info("[CONTROLLER] end endpoint confirmEmailChange");

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
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
            }
    )
    @GetMapping("/avatar/{added_date}/{file_name}")
    public ResponseEntity<?> getUserAvatar(
            @PathVariable(name = "file_name") String fileName,
            @PathVariable(name = "added_date") String addedDate
    ) throws FileNotFoundException {
        log.info("[CONTROLLER] start endpoint getAvatar with param: {}", fileName);

        ImageDto imageDto = storageService.getUserAvatar(addedDate + File.separator + fileName);

        log.info("[CONTROLLER] end endpoint getUserAvatar");

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
            description = """
                    Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Профиль ADMIN, 
                    Профиль USRER Действие: Получение информации о пользователе.
                    followers_count - количество подписчиков владельца профиля
                    followings_count - количество подписок владельца профиля
                    is_followed - подписан ли пользователь, который запрашивает профиль на его владельца
                    is_following - подписан ли владелец профиля на пользователя, который запрашивает профиль
                    """,
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
    @GetMapping("/profile/{user_id}")
    public ResponseEntity<?> getUserProfile(
            @PathVariable(name = "user_id") String userId,
            Authentication userWhoRequested
    ) {
        log.info("[CONTROLLER] start endpoint getUserProfile with param: {}", userId);

        AbstractAuthenticationToken principal = (AbstractAuthenticationToken) userWhoRequested;
        Long userWhoRequestedId = Long.valueOf(principal.getName());
        ProfileDto profileDto = userService.getProfile(Long.parseLong(userId), userWhoRequestedId);

        log.info("[CONTROLLER] end endpoint getUserProfile");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(profileDto);
    }

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Настройки профиля. Кнопка: Удалить аккаунт" +
                    " Действие: Удаление аккаунта пользователя.",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "BearerJWT")
    )
    @PreAuthorize("#userId.equals(authentication.getName())")
    @DeleteMapping("/delete/{user_id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable(name = "user_id") String userId
    ) {
        log.info("[CONTROLLER] start endpoint deleteUser with param: {}", userId);

        userService.deleteUser(Long.parseLong(userId));

        log.info("[CONTROLLER] end endpoint deleteUser");

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @GetMapping("/number/check/{number}")
    public ResponseEntity<?> checkNumber(
            @PathVariable String number
    ) {
        log.info("[CONTROLLER] start endpoint checkNumber with param: {}", number);

        boolean is = userService.isPhoneNumberExist(number);

        log.info("[CONTROLLER] end endpoint checkNumber with param: {}", number);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(is);

    }
}
