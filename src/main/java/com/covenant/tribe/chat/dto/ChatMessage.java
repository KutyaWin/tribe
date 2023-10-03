package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatMessage {

    @JsonProperty(value = "chat_id")
    Long chatId;

    @JsonProperty(value = "sender_id")
    Long senderId;

    @JsonProperty(value = "recipient_id")
    Long recipientId;

    String content;

}
