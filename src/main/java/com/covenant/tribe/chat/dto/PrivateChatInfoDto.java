package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public record PrivateChatInfoDto(
        @JsonProperty(value = "chat_id")
        Long chatId
) {
}



