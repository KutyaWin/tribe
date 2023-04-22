package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserToSendInvitationDTO {

    @JsonProperty("user_id")
    @NotNull(message = "user_id should not be null")
    Long userId;

    @JsonProperty("user_avatar")
    @Size(max = 200, message = "user_avatar must not consist of more than 200 characters")
    String userAvatar;

    @Size(max = 100, message = "username must not consist of more than 100 characters")
    @NotBlank(message = "username should not be null or empty or empty")
    String username;

    @JsonProperty("first_name")
    @Size(max = 100, message = "first_name must not consist of more than 100 characters")
    String firstName;

    @JsonProperty("last_name")
    @Size(max = 100, message = "last_name must not consist of more than 100 characters")
    String lastName;
}
