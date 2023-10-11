package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.ZonedDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatMessageDto {

    @JsonProperty(value = "chat_id")
    Long chatId;

    AuthorDto author;

    String content;

    @JsonProperty(value = "created_at")
    ZonedDateTime createdAt;

}
