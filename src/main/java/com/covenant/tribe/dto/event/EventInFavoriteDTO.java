package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventInFavoriteDTO {

    @JsonProperty(value = "event_id")
    @NotNull(message = "event_id should not be null")
    Long eventId;

    @JsonProperty(value = "event_photo")
    @Size(max = 200, message = "event_photo must not consist of more than 200 characters")
    List<String> eventPhoto;

    @JsonProperty(value = "event_name")
    @NotBlank(message = "event_name should not be null or empty")
    @Size(max = 100)
    String eventName;

    @JsonProperty(value = "event_address")
    EventAddressDTO eventAddress;

    @JsonProperty(value = "start_time")
    @DateTimeFormat(pattern = "2023-04-19T10:30")
    @Schema(pattern = "2023-04-19T10:30")
    LocalDateTime startTime;

    @JsonProperty(value = "is_finished")
    @NotNull(message = "is_finished should not be null")
    Boolean isFinished;
}
