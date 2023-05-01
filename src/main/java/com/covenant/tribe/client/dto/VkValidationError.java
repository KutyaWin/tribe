package com.covenant.tribe.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VkValidationError {
    @JsonProperty(value = "error_code")
    Integer errorCode;

    @JsonProperty(value = "error_msg")
    String errorMessage;

    @JsonProperty(value = "request_params")
    List<Map<String, String>> requestParams;
}
