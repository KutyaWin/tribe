package com.covenant.tribe.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokensDTO {

    @JsonProperty(value = "user_id")
    Long userId;

    @JsonProperty(value = "access_token")
    String accessToken;

    @JsonProperty(value = "refresh_token")
    String refreshToken;
}
