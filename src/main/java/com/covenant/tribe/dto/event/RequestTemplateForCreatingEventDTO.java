package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestTemplateForCreatingEventDTO implements Serializable {

    @JsonProperty(value = "event_type_id")
    @NotBlank(message = "event_type_name should not be null or 0")
    Long eventTypeId;

    @JsonProperty(value = "event_name")
    @NotBlank(message = "event_name should not be null or empty")
    @Size(max = 100)
    String eventName;

    @JsonProperty(value = "event_address")
    EventAddressDTO eventAddress;

    @JsonProperty(value = "start_time")
    @Schema(pattern = "2023-04-18T20:15:30.356+03:00")
    @NotNull
    OffsetDateTime startTime;

    @JsonProperty(value = "end_time")
    @Future(message = "end_time must be future")
    @Schema(pattern = "2023-04-18T20:15:30.356+03:00")
    @NotNull
    OffsetDateTime endTime;

    @JsonProperty(value = "new_event_tags_names")
    @UniqueElements(message = "All elements in event_tags_names must be unique")
    Set<@Size(max = 50, message = "tag_name must not consist of more than 50 characters") String> newEventTagNames;

    @JsonProperty(value = "event_tag_ids")
    @UniqueElements(message = "All elements in event_tags_names must be unique")
    Set<Long> eventTagIds;

    @JsonProperty(value = "description")
    String description;

    @JsonProperty(value = "avatars_for_deleting")
    List<String> avatarsForDeleting;

    @JsonProperty(value = "avatars_for_adding")
    List<String> avatarsForAdding;

    @JsonProperty(value = "invited_user_ids")
    @UniqueElements(message = "All elements in invited_user_ids must be unique")
    Set<Long> invitedUserIds;

    @JsonProperty(value = "show_event_in_search")
    @NotNull(message = "show_event_in_search should not be null.")
    Boolean showEventInSearch;

    @JsonProperty(value = "is_private")
    @NotNull(message = "is_private should not be null")
    Boolean isPrivate;

    @JsonProperty(value = "send_to_all_users_by_interests")
    @NotNull(message = "send_to_all_users_by_interests should not be null.")
    Boolean sendToAllUsersByInterests;

    @JsonProperty(value = "is_eighteen_year_limit")
    @NotNull(message = "is_eighteen_year_limit should not be null.")
    Boolean isEighteenYearLimit;

    @JsonProperty(value = "organizer_id")
    @Min(value = 1, message = "organizer_id should be greater than 0")
    Long organizerId;
}
