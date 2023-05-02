package com.covenant.tribe.dto.user;

import com.covenant.tribe.dto.event.EventAddressDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileDTO implements Serializable {

    @JsonProperty("bluetooth_id")
    @NotBlank(message = "bluetooth_id should not be null or empty")
    @Size(max = 100, message = "bluetooth_id must not consist of more than 100 characters")
    String bluetoothId;

    @Size(max = 100, message = "username must not consist of more than 100 characters")
    @NotBlank(message = "username should not be null or empty")
    String username;

    @JsonProperty("first_name")
    @Size(max = 100, message = "first_name must not consist of more than 100 characters")
    String firstName;

    @JsonProperty("last_name")
    @Size(max = 100, message = "last_name must not consist of more than 100 characters")
    String lastName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "birthday date should be past")
    LocalDate birthday;

    @JsonProperty("user_avatar")
    @Size(max = 200, message = "user_avatar must not consist of more than 200 characters")
    String userAvatar;

    @JsonProperty("interesting_event_type")
    @UniqueElements(message = "All elements in favorite_events_id must be unique")
    Set<String> interestingEventType = new HashSet<>();

    @JsonProperty(value = "number_of_following")
    Long numberOfFollowing;

    @JsonProperty(value = "number_of_followers")
    Long numberOfFollowers;

    @JsonProperty(value = "events_where_user_as_participant_in_profile_user")
    List<EventInProfileUser> eventsWhereUserAsParticipantInProfileUser = new ArrayList<>();

    @JsonProperty(value = "number_not_viewed_events_and_invitation_events_in_profile_user")
    List<NumberNotViewedEventAndEventForInvitationInProfileUser> numberNotViewedEventsAndInvitationEventsInProfileUser = new ArrayList<>();

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class NumberNotViewedEventAndEventForInvitationInProfileUser implements Serializable {

        @JsonProperty(value = "number_not_viewed_events")
        Long numberNotViewedEvents;

        @JsonProperty(value = "invitation_events_in_profile_user")
        List<EventInProfileUser> invitationEventsInProfileUser;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class EventInProfileUser implements Serializable {

        Long id;

        @JsonProperty(value = "event_photo_url")
        @Size(max = 200, message = "event_photo_url must not consist of more than 200 characters")
        String eventPhotoUrl;

        @JsonProperty(value = "event_name")
        @NotBlank(message = "event_name should not be null or empty")
        @Size(max = 100)
        String eventName;

        @JsonProperty(value = "event_address")
        EventAddressDTO eventAddress;

        @JsonProperty(value = "start_time")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime startTime;

        @JsonProperty(value = "is_event_complete")
        @NotNull(message = "is_event_complete should not be null.")
        Boolean isEventComplete;

    }
}
