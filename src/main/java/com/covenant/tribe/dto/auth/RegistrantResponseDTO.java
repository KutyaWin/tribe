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
public class RegistrantResponseDTO implements Serializable {
    @JsonProperty("registrant_id")
    Long registrantId;

    int code; //TODO delete before release
}