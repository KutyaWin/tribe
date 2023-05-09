package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UsersWhoParticipantsOfEventDTO implements Serializable {

    @JsonProperty(value = "participant_id")
    @NotNull(message = "id should not be null")
    Long participantId;

    @JsonProperty(value = "participant_avatar_url")
    String participantAvatarUrl;

}
