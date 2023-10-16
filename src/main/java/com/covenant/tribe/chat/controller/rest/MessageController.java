package com.covenant.tribe.chat.controller.rest;

import com.covenant.tribe.chat.dto.ChatMessageDto;
import com.covenant.tribe.chat.service.ChatMessageService;
import com.covenant.tribe.util.security.TokenUtil;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/chat/message")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Chat")
public class MessageController {

    ChatMessageService chatMessageService;

    @Operation(
            description = """
                    Категория: Чат. Экран: Чат. Действие: Получение всех сообщений чата.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = ChatMessageDto.class
                                            )
                                    )
                            )
                    )
            },
            security = @SecurityRequirement(name = "Bearer JWT")
    )
    @GetMapping("/{chat_id}")
    public ResponseEntity<?> getMessagesByChatId(
            @PathVariable(value = "chat_id") Long chatId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "30") Integer size
    ) {
        log.info("[CONTROLLER] start endpoint getMessagesByChatId");

        Long userId = TokenUtil.getUserIdFromToken(SecurityContextHolder.getContext());
        Pageable pageable = Pageable.ofSize(size).withPage(page);

        Page<ChatMessageDto> messages = chatMessageService.getMessagesByChatId(userId, chatId, pageable);

        log.info("[CONTROLLER] end endpoint getMessagesByChatId");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(messages);
    }

    @Operation(
            description = """
                    Категория: Чат. Экран: Чат. Действие: Добавление/обновление последнего
                    прочитанного сообщения.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            },
            security = @SecurityRequirement(name = "Bearer JWT")
    )
    @PatchMapping("/{chat_id}/{message_id}/read")
    public ResponseEntity<?> setLastReadMessage(
            @PathVariable(value = "chat_id") Long chatId,
            @PathVariable(value = "message_id") Long messageId
    ) {
        log.info("[CONTROLLER] start endpoint setLastReadMessage");

        Long userId = TokenUtil.getUserIdFromToken(SecurityContextHolder.getContext());

        chatMessageService.setLastReadMessage(userId, chatId, messageId);

        log.info("[CONTROLLER] end endpoint setLastReadMessage");

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
