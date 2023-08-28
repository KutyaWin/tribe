package com.covenant.tribe.dto.event;

import com.covenant.tribe.domain.event.EventStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class EventInUserProfileDTO implements Serializable {

        Long id;

        @JsonProperty(value = "event_photo_url")
        List<String> eventPhotoUrl;

        @JsonProperty(value = "event_name")
        String eventName;

        @JsonProperty(value = "city")
        String city;

        @JsonProperty(value = "start_time")
        LocalDateTime startTime;

        @JsonProperty(value = "event_status")
        EventStatus eventStatus;

        @JsonProperty(value = "is_finished")
        boolean isFinished;
}
