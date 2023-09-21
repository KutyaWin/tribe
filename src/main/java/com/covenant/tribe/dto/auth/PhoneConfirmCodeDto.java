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
public class PhoneConfirmCodeDto {

    @Min(value = 1000, message = "verificationCode should be at least 1000")
    @Max(value = 9999, message = "verificationCode should be at most 9999")
    @JsonProperty(value = "verification_code")
    Integer verificationCode;

    @NotBlank(message = "Phone number should not be blank or null")
    @JsonProperty(value = "phone_number")
    String phoneNumber;

    @NotBlank(message = "FirebaseId should not be blank or null")
    @JsonProperty(value = "firebase_id")
    String firebaseId;

}
