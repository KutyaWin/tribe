package com.covenant.tribe.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailLoginDTO implements Serializable {

    @NotBlank(message = "email should not be null or empty")
    @Email(message = "email should be valid")
    String email;

    @NotBlank(message = "password should not be null or empty")
    @ToString.Exclude
    String password;

}
