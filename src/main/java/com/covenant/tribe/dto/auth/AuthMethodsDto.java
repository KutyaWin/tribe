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

    @JsonProperty("has_email_authentication")
    boolean hasEmailAuthentication;

    @JsonProperty("has_google_authentication")
    boolean hasGoogleAuthentication;

    @JsonProperty("has_vk_authentication")
    boolean hasVkAuthentication;

    @JsonProperty("has_whatsapp_authentication")
    boolean hasWhatsAppAuthentication;

    @JsonProperty("has_telegram_authentication")
    boolean hasTelegramAuthentication;

}
