package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantPreviewDTO {

    @JsonProperty(value = "participant_id")
    Long participantId;

    @JsonProperty(value = "participant_avatar_url")
    String participantAvatarUrl;

}
