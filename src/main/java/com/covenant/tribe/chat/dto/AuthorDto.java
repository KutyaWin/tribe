package com.covenant.tribe.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthorDto {

    @JsonProperty(value = "author_id")
    Long authorId;

    @JsonProperty(value = "avatar_url")
    String avatarUrl;

    String username;

    String firstname;

    String lastname;

}
