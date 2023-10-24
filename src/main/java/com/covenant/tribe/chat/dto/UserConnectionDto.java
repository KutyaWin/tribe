package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserConnectionDto(
        @JsonProperty(value = "user_id")
        Long userId,
        @JsonProperty(value = "is_online")
        boolean isOnline
) {
}
