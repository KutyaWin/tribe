package com.covenant.tribe.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordDTO implements Serializable {

    @NotNull(message = "userId should not be null or empty")
    @JsonProperty(value = "user_id")
    Long userId;

    @NotBlank(message = "password should not be null or empty")
    @JsonProperty(value = "old_password")
    String oldPassword;

    @Length(min = 6, message = "password should not be less than 6 characters")
    @NotBlank(message = "password should not be null or empty")
    @JsonProperty(value = "new_password")
    @ToString.Exclude
    String newPassword;

}
