package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventVerificationDTO {

    @JsonProperty(value = "event_id")
    Long eventId;

    @JsonProperty(value = "organizer_id")
    Long organizerId;

    @JsonProperty(value = "event_photos")
    List<String> eventPhotos;

    @JsonProperty(value = "created_at")
    LocalDateTime createdAt;

    @JsonProperty(value = "event_address")
    EventAddressDTO eventAddress;

    @JsonProperty(value = "event_name")
    String eventName;

    @JsonProperty(value = "event_description")
    String eventDescription;

    @JsonProperty(value = "start_time")
    LocalDateTime startTime;

    @JsonProperty(value = "end_time")
    LocalDateTime endTime;

    @JsonProperty(value = "eighteen_year_limit")
    boolean eighteenYearLimit;

    @JsonProperty(value = "event_type")
    String eventType;

    @JsonProperty(value = "event_tags")
    List<String> eventTags;

}
