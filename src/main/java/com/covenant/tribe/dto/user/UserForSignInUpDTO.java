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
public class  UserForSignInUpDTO implements Serializable {

    @JsonProperty("firebase_id")
    @NotBlank(message = "firebase_id should not be null or empty")
    @Size(max = 100, message = "firebase_id must not consist of more than 100 characters")
    String firebaseId;

    @Size(max = 50, message = "Email must not consist of more than 50 characters")
    String email;

    @NotBlank(message = "username should not be null or empty")
    @Size(max = 50, message = "username must not consist of more than 50 characters")
    String username;

    @JsonProperty("phone_number")
    @Schema(example = "+380937419055")
    String phoneNumber;
}
