package com.covenant.tribe.client.whatsapp.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhatsAppMessageTemplateDto {

    String name;

    WhatsAppMessageLanguageDto language;

    List<WhatsAppComponentDto> components;

}
