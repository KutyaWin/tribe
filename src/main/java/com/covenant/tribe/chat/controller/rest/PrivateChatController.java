package com.covenant.tribe.chat.controller.rest;

import com.covenant.tribe.chat.dto.ChatDto;
import com.covenant.tribe.chat.dto.PrivateChatInfoDto;
import com.covenant.tribe.chat.dto.PrivateChatInvitedUserDto;
import com.covenant.tribe.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateChatController {

    ChatService chatService;

    @Operation(
            description = "Категория: Профиль/ADMIN/USER/FOLLOWERS/MESSAGES/. Экран: Любой, на котором можно создать новый чат" +
                    ". Кнопка: Вход в чат, или создание чата. Действие: Создание нового или перемещение в уже " +
                    "существующий чат",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    schema = @Schema(implementation = PrivateChatInfoDto.class)
                            )
                    )
            }
    )
    @PostMapping("/chat")
    public ResponseEntity<?> createPrivateChat(
            @RequestBody PrivateChatInvitedUserDto invitedUserDto
    ) {

        log.info("[CONTROLLER] start endpoint getChatIdByParticipants");

        Long chatCreatorId = getUserIdFromToken();

        PrivateChatInfoDto chatInfoByParticipants = chatService
                .createPrivateChat(invitedUserDto, chatCreatorId);

        log.info("[CONTROLLER] end endpoint getChatIdByParticipants");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(chatInfoByParticipants);
    }

    @Operation(
            description = "Категория: CHAT. Экран: Чат. Действие: Получение всех чатов при входе во вкладку чат.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = ChatDto.class
                                            )
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "Bearer JWT")
    )
    @GetMapping("/chats")
    public ResponseEntity<?> getChatsByUser(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        log.info("[CONTROLLER] start endpoint getChatsByUser");

        Long userId = getUserIdFromToken();

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<ChatDto> chats = chatService.getChatsByUserId(userId, pageable);

        log.info("[CONTROLLER] end endpoint getChatsByUser");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(chats);
    }

    private Long getUserIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(auth.getName());
    }

}
