package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserWhoInvitedToEventAsParticipantDTO implements Serializable {

    @Size(max = 100, message = "username must not consist of more than 100 characters")
    @NotBlank(message = "username should not be null or empty or empty")
    String username;
}
