package com.covenant.tribe.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrantRequestDTO {

    @NotBlank(message = "email should not be null or empty")
    String email;

    @NotBlank(message = "password should not be null or empty")
    String password;

    @Size(max = 100, message = "username must not consist of more than 100 characters")
    @NotBlank(message = "username should not be null or empty")
    String username;

}
