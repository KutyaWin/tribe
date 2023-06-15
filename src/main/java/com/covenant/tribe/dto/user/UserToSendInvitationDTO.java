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
    Long userId;

    @JsonProperty("user_avatar")
    String userAvatar;

    String username;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;
}
