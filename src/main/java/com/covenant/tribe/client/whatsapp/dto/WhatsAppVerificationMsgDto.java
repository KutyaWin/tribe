package com.covenant.tribe.client.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhatsAppVerificationMsgDto {

    @JsonProperty("messaging_product")
    @Builder.Default
    String messagingProduct = "whatsapp";

    @JsonProperty("recipient_type")
    @Builder.Default
    String recipientType = "individual";

    String to;

    @Builder.Default
    String type = "template";

    WhatsAppMessageTemplateDto template;







}
