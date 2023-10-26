package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LastReadMessageDto(
        @JsonProperty(value = "user_who_read_id")
        Long userWhoReadId,
        @JsonProperty(value = "message_id")
        Long messageId,
        @JsonProperty(value = "chat_id")
        Long chatId
) {
}
