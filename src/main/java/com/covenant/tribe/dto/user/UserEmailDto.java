package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEmailDto {

    @JsonProperty("user_id")
    @NotEmpty(message = "user_id can not be empty or null")
    String userId;

    @JsonProperty("old_email")
    @Email(message = "email is not valid")
    @NotEmpty(message = "email can not be empty or null")
    String oldEmail;

    @JsonProperty("new_email")
    @Email(message = "email is not valid")
    @NotEmpty(message = "email can not be empty or null")
    String newEmail;

}
