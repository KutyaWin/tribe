package com.covenant.tribe.client.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhatsAppVerificationSuccessResponseDto {

    @JsonProperty("messaging_product")
    String messagingResponse;

    List<WhatsAppMessageContactDto> contacts;

    List<WhatsAppMessageDto> messages;

}
