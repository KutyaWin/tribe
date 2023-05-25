package com.covenant.tribe.dto.user;

import com.covenant.tribe.dto.auth.AuthMethodsDto;
import com.covenant.tribe.dto.event.EventTypeInfoDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserProfileGetDto implements Serializable {

    @JsonProperty("avatar_url")
    String avatarUrl;

    String username;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    LocalDate birthday;

    @JsonProperty("interesting_event_type")
    @Builder.Default
    List<EventTypeInfoDto> interestingEventType = new ArrayList<>();

    @Builder.Default
    List<ProfessionDto> professions = new ArrayList<>();

    String email;

    @JsonProperty("phone_number")
    String phoneNumber;

    @JsonProperty("available_auth_methods")
    AuthMethodsDto availableAuthMethods;

    @JsonProperty("is_geolocation_available")
    boolean isGeolocationAvailable;

}
