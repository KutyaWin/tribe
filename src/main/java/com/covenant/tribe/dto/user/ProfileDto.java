package com.covenant.tribe.dto.user;

import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.dto.event.EventTypeInfoDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDto implements Serializable {

    @JsonProperty("user_id")
    Long userId;

    @JsonProperty("avatar_url")
    String avatarUrl;

    String username;

    @JsonProperty("full_name")
    String fullName;

    int age;

    List<String> professions;

    @JsonProperty("subscribers_count")
    int followersCount;

    @JsonProperty("subscribed_people_count")
    int followingCount;

    List<String> interests;

}
