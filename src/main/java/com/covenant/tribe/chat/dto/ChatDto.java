package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

import java.time.ZonedDateTime;

public record ChatDto(

        @JsonProperty(value = "chat_id")
        Long chatId,
        @JsonProperty(value = "author_name")
        String authorName,
        @JsonProperty(value = "author_surname")
        String authorSurname,
        @JsonProperty(value = "author_id")
        Long authorId,
        @JsonProperty(value = "is_second_participant_online")
        Boolean isSecondParticipantOnline,
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
