package com.covenant.tribe.dto.event;

import com.covenant.tribe.dto.user.UserWhoInvitedToEventAsParticipantDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestTemplateForCreatingEventDTO implements Serializable {

    @JsonProperty(value = "event_type_name")
    @NotBlank(message = "event_type_name should not be null or empty")
    @Size(max = 50)
    String eventTypeName;

    @JsonProperty(value = "event_photo")
    @Size(max = 200, message = "event_photo must not consist of more than 200 characters")
    String eventPhoto;

    @JsonProperty(value = "event_name")
    @NotBlank(message = "event_name should not be null or empty")
    @Size(max = 100)
    String eventName;

    @JsonProperty(value = "event_address")
    EventAddressDTO eventAddress;

    @JsonProperty(value = "start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Schema(pattern = "2023-04-19T10:30")
    LocalDateTime startTime;

    @JsonProperty(value = "end_time")
    @Future(message = "end_time must be future")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Schema(pattern = "2023-04-19T20:30")
    LocalDateTime endTime;

    @JsonProperty(value = "event_tags_names")
    @UniqueElements(message = "All elements in event_tags_names must be unique")
    Set<@Size(max = 50, message = "tag_name must not consist of more than 50 characters") String> eventTagsNames = new HashSet<>();

    @JsonProperty(value = "description")
    String description;

    @JsonProperty(value = "users_who_invited")
    @UniqueElements(message = "All elements in users_who_invited must be unique")
    Set<UserWhoInvitedToEventAsParticipantDTO> usersWhoInvited = new HashSet<>();

    @JsonProperty(value = "show_event_in_search")
    @NotNull(message = "show_event_in_search should not be null.")
    Boolean showEventInSearch;

    @JsonProperty(value = "send_to_all_users_by_interests")
    @NotNull(message = "send_to_all_users_by_interests should not be null.")
    Boolean sendToAllUsersByInterests;

    @JsonProperty(value = "eighteen_year_limit")
    @NotNull(message = "eighteen_year_limit should not be null.")
    Boolean eighteenYearLimit;

    @JsonProperty(value = "organizer_username")
    @NotBlank(message = "organizer_username should not be null or empty")
    @Size(max = 100, message = "organizer_username must not consist of more than 100 characters")
    String organizerUsername;
}
