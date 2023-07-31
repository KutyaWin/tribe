package com.covenant.tribe.dto.event;

import com.covenant.tribe.dto.user.UsersWhoParticipantsOfEventDTO;
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
public class DetailedEventInSearchDTO {

    @JsonProperty(value = "event_id")
    @NotNull(message = "event_id should not be null")
    Long eventId;

    @JsonProperty(value = "event_photo")
    @Size(max = 200, message = "event_photo must not consist of more than 200 characters")
    List<String> eventPhoto;

    @JsonProperty(value = "favorite_event")
    @NotNull(message = "favorite_event should not be null")
    Boolean favoriteEvent;


    @JsonProperty(value = "organizer_photo")
    @Size(max = 200, message = "organizer_photo must not consist of more than 200 characters")
    String organizerPhoto;

    @JsonProperty(value = "event_name")
    @NotBlank(message = "event_name should not be null or empty")
    @Size(max = 100)
    String eventName;

    @JsonProperty(value = "organizer_username")
    @NotBlank(message = "organizer_username should not be null or empty")
    @Size(max = 100, message = "organizer_username must not consist of more than 100 characters")
    String organizerUsername;

    @JsonProperty(value = "organizer_id")
    Long organizerId;

    @JsonProperty(value = "event_address")
    EventAddressDTO eventAddress;

    @JsonProperty(value = "start_time")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(pattern = "2023-04-18T20:15:30.356+03:00")
    OffsetDateTime startTime;

    @JsonProperty(value = "event_duration")
    @NotBlank(message = "event_duration should not be null or empty")
    @Pattern(regexp = "^PT[0-9]*[HMS]*$", message = "event_duration must be in format PT[0-9]*[HMS], for example PT1H30M")
    @Schema(example = "PT1H30M")
    private String eventDuration;

    @JsonProperty(value = "description")
    String description;

    @JsonProperty(value = "users_who_participants_of_event")
    @UniqueElements(message = "All elements in usersWhoParticipantsOfEvent must be unique")
    Set<UsersWhoParticipantsOfEventDTO> usersWhoParticipantsOfEvent = new HashSet<>();

    @JsonProperty(value = "is_private")
    Boolean isPrivate;

    @JsonProperty(value = "is_free")
    Boolean isFree;

    @JsonProperty(value = "is_finished")
    Boolean isFinished;

    @JsonProperty(value = "is_participant")
    Boolean isParticipant;

    @JsonProperty(value = "is_want_to_go")
    Boolean isWantToGo;

    @JsonProperty(value = "is_invited")
    Boolean isInvited;
}
