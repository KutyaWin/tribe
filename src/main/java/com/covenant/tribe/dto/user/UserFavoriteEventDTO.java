package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFavoriteEventDTO implements Serializable {

    @JsonProperty(value = "user_id")
    Long userId;

    @JsonProperty(value = "event_id")
    Long eventId;
}
