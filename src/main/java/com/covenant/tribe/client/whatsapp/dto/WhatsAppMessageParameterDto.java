package com.covenant.tribe.client.whatsapp.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhatsAppMessageParameterDto {

    String type;

    String text;

}
