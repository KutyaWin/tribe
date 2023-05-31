package com.covenant.tribe.dto.auth;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmRegistrationDTO {

    @Min(value = 1, message = "registrantId should be at least 1")
    @JsonProperty(value = "registrant_id")
    Long registrantId;

    @Min(value = 1000, message = "verificationCode should be at least 1000")
    @Max(value = 9999, message = "verificationCode should be at most 9999")
    @JsonProperty(value = "verification_code")
    Integer verificationCode;

    @NotBlank(message = "firebaseId should not be null or empty")
    @JsonProperty(value = "firebase_id")
    String firebaseId;

}
