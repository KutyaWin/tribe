package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileUpdateDto implements Serializable {

    @JsonProperty("user_id")
    Long userId;

    @JsonProperty("user_avatar")
    @Size(max = 200, message = "user_avatar must not consist of more than 200 characters")
    String userAvatar;

    @JsonProperty("avatars_filenames_for_deleting")
    List<String> avatarsFilenamesForDeleting;

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

    @JsonProperty("interesting_event_type")
    @UniqueElements(message = "All elements in favorite_events_id must be unique")
    List<Long> interestingEventType;

    @JsonProperty("profession_ids")
    @UniqueElements(message = "All elements in favorite_events_id must be unique")
    List<Long> professionIds;

    @JsonProperty("new_professions")
    @UniqueElements(message = "All elements in new_professions must be unique")
    List<String> newProfessions;

    @JsonProperty("is_geolocation_available")
    boolean isGeolocationAvailable;

}
