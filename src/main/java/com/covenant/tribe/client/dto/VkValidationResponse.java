package com.covenant.tribe.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VkValidationResponse {
    Long date;
    Long expire;
    Integer success;
    @JsonProperty(value = "user_id")
    Long userId;
}
