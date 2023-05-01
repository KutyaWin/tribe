package com.covenant.tribe.dto.auth;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmRegistrationDTO {

    @NotBlank(message = "registrantId should not be null or empty")
    @JsonProperty(value = "registrant_id")
    Long registrantId;

    @NotBlank(message = "verificationCode should not be null or empty")
    @JsonProperty(value = "verification_code")
    Integer verificationCode;

    @NotBlank(message = "firebaseId should not be null or empty")
    @JsonProperty(value = "firebase_id")
    String firebaseId;

    @NotBlank(message = "bluetoothId should not be null or empty")
    @JsonProperty(value = "bluetooth_id")
    String bluetoothId;

}
