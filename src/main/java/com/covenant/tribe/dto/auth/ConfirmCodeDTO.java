package com.covenant.tribe.dto.auth;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
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
public class ConfirmCodeDTO {

    @Min(value = 1000, message = "verificationCode should be at least 1000")
    @Max(value = 9999, message = "verificationCode should be at most 9999")
    @JsonProperty(value = "verification_code")
    Integer verificationCode;

    @NotBlank(message = "Email should not be blank or null")
    @Email(message = "Email should be valid")
    String email;



}
