package com.covenant.tribe.dto.event;

import com.covenant.tribe.dto.user.UserToSendInvitationDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventDto implements Serializable {

    @NotNull(message = "event_id is required")
    @JsonProperty("event_id")
    Long eventId;

    @NotNull(message = "organizer_id is required")
    @JsonProperty("organizer_id")
    String organizerId;

    @NotNull(message = "event_type_id is required")
    @JsonProperty("event_type_id")
    Long eventTypeId;

    @JsonProperty("avatars_for_deleting")
    List<String> avatarsForDeleting;

    @JsonProperty("avatars_for_adding")
    List<String> avatarsForAdding;

    @NotEmpty(message = "name is required")
    String name;

    @NotNull(message = "address is required")
    @JsonProperty("address_dto")
    EventAddressDTO addressDTO;

    @NotNull(message = "start_date_time is required")
    @JsonProperty("event_start_date_time")
    OffsetDateTime startDateTime;

    @NotNull(message = "end_date_time is required")
    @JsonProperty("event_end_date_time")
    OffsetDateTime endDateTime;

    @JsonProperty("tag_ids_for_deleting")
    List<Long> tagIdsForDeleting;

    @JsonProperty("tag_ids_for_adding")
    List<Long> tagIdsForAdding;

    @JsonProperty("new_tags")
    Set<String> newTags;

    @NotEmpty(message = "description is required")
    String description;

    @JsonProperty("participant_ids_for_adding")
    List<Long> participantIdsForAdding;

    @JsonProperty("participant_ids_for_deleting")
    List<Long> participantIdsForDeleting;

    @JsonProperty("is_private")
    boolean isPrivate;

    @JsonProperty("is_show_in_search")
    boolean isShowInSearch;

    @JsonProperty("is_send_by_interests")
    boolean isSendByInterests;

    @JsonProperty("is_eighteen_year_limit")
    boolean isEighteenYearLimit;

    @JsonProperty("has_alcohol")
    boolean hasAlcohol;

}
