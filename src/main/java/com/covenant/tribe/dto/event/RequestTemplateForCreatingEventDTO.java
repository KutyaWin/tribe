package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestTemplateForCreatingEventDTO implements Serializable {

    @JsonProperty(value = "event_type_id")
    @NotNull(message = "event_type_name should not be null or 0")
    Long eventTypeId;

    @JsonProperty(value = "event_name")
    @NotBlank(message = "event_name should not be null or empty")
    @Size(max = 100, min = 2)
    String eventName;

    @JsonProperty(value = "event_address")
    @NotNull(message = "event_address should not be null")
    EventAddressDTO eventAddress;

    @JsonProperty(value = "start_time")
    @Schema(pattern = "2023-04-18T20:15:30.356")
    @NotNull
    LocalDateTime startTime;

    @JsonProperty(value = "end_time")
    @Future(message = "end_time must be future")
    @Schema(pattern = "2023-04-18T20:15:30.356")
    @NotNull
    LocalDateTime endTime;

    @JsonProperty(value = "new_event_tags_names")
    @UniqueElements(message = "All elements in event_tags_names must be unique")
    Set<@Size(max = 50, message = "tag_name must not consist of more than 50 characters") String> newEventTagNames;

    @JsonProperty(value = "event_tag_ids")
    @UniqueElements(message = "All elements in event_tags_names must be unique")
    Set<Long> eventTagIds;

    @JsonProperty(value = "description")
    @NotNull(message = "description should not be null")
    String description;

    @JsonProperty(value = "contact_info")
    List<EventContactInfoDto> eventContactInfoDtos;

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

    @JsonProperty(value = "has_alcohol")
    @NotNull(message = "has_alcohol should not be null")
    Boolean hasAlcohol;

    @JsonProperty(value = "send_to_all_users_by_interests")
    @NotNull(message = "send_to_all_users_by_interests should not be null.")
    Boolean sendToAllUsersByInterests;

    @JsonProperty(value = "is_eighteen_year_limit")
    @NotNull(message = "is_eighteen_year_limit should not be null.")
    Boolean isEighteenYearLimit;

    @JsonProperty(value = "organizer_id")
    @Min(value = 1, message = "organizer_id should be greater than 0")
    @NotNull(message = "organizer_id should not be null")
    Long organizerId;

    @JsonProperty(value = "is_free")
    @NotNull(message = "is_free should not be null")
    Boolean isFree;

    @JsonProperty(value = "time_zone")
    @NotNull(message = "time_zone should not be null")
    @Schema(pattern = "GMT+03:00")
    String timeZone;
}
