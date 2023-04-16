package com.covenant.tribe.dto.event;

import com.covenant.tribe.dto.user.ParticipantPreviewDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDTO {

    Long id;

    String name;

    @JsonProperty(value = "organizer_nickname")
    String organizerNickname;

    @JsonProperty(value = "organizer_avatar_url")
    String organizerAvatarUrl;

    @JsonProperty(value = "event_photo_url")
    String eventPhotoUrl;

    @JsonProperty(value = "is_in_user_favorite_events")
    Boolean isInUserFavoriteEvents;

    String city;

    String startDate;

    String startTime;

    String description;

    Double latitude;

    Double longitude;

    @JsonProperty(value = "lost_ten_participant_ids")
    List<ParticipantPreviewDTO> lostTenParticipantIds;

    @JsonProperty(value = "participants_amount")
    Integer participantsAmount;

    @JsonProperty(value = "is_finished")
    Boolean isFinished;


}
