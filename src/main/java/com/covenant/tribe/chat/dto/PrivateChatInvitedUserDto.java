package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PrivateChatInvitedUserDto(
        @JsonProperty(value = "invited_user_id") Long invitedUserId
) {

}
