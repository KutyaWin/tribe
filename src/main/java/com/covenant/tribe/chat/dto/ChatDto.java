package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
public record ChatDto(

        @JsonProperty(value = "chat_id")
        Long chatId,
        @JsonProperty(value = "last_message")
        String lastMessage,
        @JsonProperty(value = "last_message_time")
        ZonedDateTime lastMessageTime,
        @JsonProperty(value = "unread_message_count")
        Integer unreadMessageCount,
        @JsonProperty(value = "chat_avatar")
        String chatAvatar,
        @JsonProperty(value = "is_group")
        Boolean isGroup,
        @JsonProperty(value = "chat_name")
        String chatName
) {
}
