package com.covenant.tribe.dto.event;

import com.covenant.tribe.domain.event.EventStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
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
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @Schema(pattern = "2023-04-18T20:15:30.356+03:00")
        OffsetDateTime startTime;

        @JsonProperty(value = "event_status")
        EventStatus eventStatus;

        @JsonProperty(value = "is_viewed")
        Boolean isViewed;

}
