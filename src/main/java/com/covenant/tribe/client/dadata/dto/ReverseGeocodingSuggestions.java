package com.covenant.tribe.client.dadata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReverseGeocodingSuggestions {

    String value;

    @JsonProperty(value = "unrestricted_value")
    String unrestrictedValue;

    ReverseGeocodingData data;

}
