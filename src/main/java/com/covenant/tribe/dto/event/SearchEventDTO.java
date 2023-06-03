package com.covenant.tribe.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
}
