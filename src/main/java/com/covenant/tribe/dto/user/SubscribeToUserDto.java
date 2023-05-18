package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscribeToUserDto {

    @JsonProperty("follower_user_id")
    @NotNull(message = "follower_user_id must not be null")
    Long followerUserId;

    @NotNull(message = "following_user_id must not be null")
    @JsonProperty("following_user_id")
    Long followingUserId;

}
