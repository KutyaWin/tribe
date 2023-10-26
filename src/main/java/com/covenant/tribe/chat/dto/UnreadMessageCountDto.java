package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UnreadMessageCountDto(
        @JsonProperty(value = "unread_message_count")
        int unreadMessageCount
) {
}
