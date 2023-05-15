package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @JsonProperty(value = "created_at")
    OffsetDateTime createdAt;

    @JsonProperty(value = "event_address")
    EventAddressDTO eventAddress;

    @JsonProperty(value = "event_name")
    String eventName;

    @JsonProperty(value = "event_description")
    String eventDescription;

    @JsonProperty(value = "start_time")
    OffsetDateTime startTime;

    @JsonProperty(value = "end_time")
    OffsetDateTime endTime;

    @JsonProperty(value = "eighteen_year_limit")
    boolean eighteenYearLimit;

    @JsonProperty(value = "event_type")
    String eventType;

    @JsonProperty(value = "event_tags")
    List<String> eventTags;

}
