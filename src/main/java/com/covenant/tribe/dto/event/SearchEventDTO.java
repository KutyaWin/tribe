package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchEventDTO implements Serializable {

    @JsonProperty(value = "event_id")
    @NotNull(message = "event_id should not be null")
    Long eventId;

    @JsonProperty(value = "event_photo")
    @Size(max = 200, message = "event_photo must not consist of more than 200 characters")
    String eventPhoto;

    @JsonProperty(value = "favorite_event")
    @NotNull(message = "favorite_event should not be null.")
    Boolean favoriteEvent;

    @JsonProperty(value = "event_name")
    @NotBlank(message = "event_name should not be null or empty")
    @Size(max = 100)
    String eventName;

    @JsonProperty(value = "event_address")
    EventAddressDTO eventAddress;

    @JsonProperty(value = "start_time")
    @DateTimeFormat(pattern = "2023-04-19T10:30")
    LocalDateTime startTime;
}
