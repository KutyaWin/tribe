package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSubscriberDto {

    @JsonProperty("user_id")
    Long userId;

    @JsonProperty("user_avatar")
    String userAvatar;

    String username;

    @JsonProperty("first_name")
    String firstName;

    @JsonProperty("last_name")
    String lastName;

    @JsonProperty("is_user_subscribe_to_subscriber")
    Boolean isUserSubscribeToSubscriber;

    @JsonProperty("chat_id")
    Long chatId;
}
