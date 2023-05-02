package com.covenant.tribe.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrantRequestDTO implements Serializable {

    @NotBlank(message = "email should not be null or empty")
    String email;

    @NotBlank(message = "password should not be null or empty")
    @ToString.Exclude
    String password;

    @Size(max = 100, message = "username must not consist of more than 100 characters")
    @NotBlank(message = "username should not be null or empty")
    String username;

}
