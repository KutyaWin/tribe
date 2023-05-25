package com.covenant.tribe.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthMethodsDto implements Serializable {

    @JsonProperty("is_email_available")
    boolean isEmailAvailable;

    @JsonProperty("is_google_available")
    boolean isGoogleAvailable;

    @JsonProperty("is_vk_available")
    boolean isVkAvailable;

    @JsonProperty("is_whatsapp_available")
    boolean isWhatsAppAvailable;

    @JsonProperty("is_telegram_available")
    boolean isTelegramAvailable;

}
