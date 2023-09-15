package com.covenant.tribe.dto.event;

import com.covenant.tribe.domain.event.ContactType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventContactInfoDto {

    @JsonProperty(value = "contact_type")
    ContactType contactType;

    String contact;

}
