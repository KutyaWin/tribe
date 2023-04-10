package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFavoriteEventDTO {

    @JsonProperty(value = "user_id")
    Long userId;

    @JsonProperty(value = "event_id")
    Long eventId;
}
