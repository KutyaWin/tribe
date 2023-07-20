package com.covenant.tribe.dto.event;

import com.covenant.tribe.dto.user.UsersWhoParticipantsOfEventDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchEventDTO implements Serializable {

    Long eventId;

    List<String> avatarUrl;

    Boolean favoriteEvent;

    Boolean viewEvent;

    String eventName;

    String eventType;

    String organizerUsername;

    String description;

    EventAddressDTO eventAddress;

    LocalDateTime startTime;

    Boolean isPrivate;

    Boolean isPresenceOfAlcohol = false;

    @JsonProperty(value = "participants")
    Set<UsersWhoParticipantsOfEventDTO> participants = new HashSet<>();
}
