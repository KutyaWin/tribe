package com.covenant.tribe.client.whatsapp.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhatsAppMessageParameter {

    String type;

    String text;

}
