package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TESTUserForSignUpDTO implements Serializable {

    @JsonProperty("bluetooth_id")
    @NotBlank(message = "bluetooth_id should not be null or empty or empty")
    @Size(max = 100, message = "bluetooth_id must not consist of more than 100 characters")
    String bluetoothId;

    @Size(max = 100, message = "username must not consist of more than 100 characters")
    @NotBlank(message = "username should not be null or empty or empty")
    String username;

    @Size(max = 50, message = "Email must not consist of more than 50 characters")
    @NotBlank(message = "Email should not be null or empty")
    @Email(message = "Please provide a valid email address")
    String email;

    @NotNull(message = "Password should not be null")
    @Size(max = 500, message = "Password must not consist of more than 500 characters")
    String password;

    @JsonProperty("phone_number")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,17}$",
            message = "Phone number can start with the symbol +, must have from 7 to 14 numbers")
    @Schema(example = "+380937419055")
    String phoneNumber;
}
