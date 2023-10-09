package com.covenant.tribe.dto.user;

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

    Integer age;

    List<String> professions;

    @JsonProperty("followers_count")
    int followersCount;

    @JsonProperty("followings_count")
    int followingCount;

    @JsonProperty("is_followed")
    boolean isFollowed;

    @JsonProperty("is_following")
    boolean isFollowing;

    List<String> interests;

    @JsonProperty("chat_id")
    Long chatId;

}
