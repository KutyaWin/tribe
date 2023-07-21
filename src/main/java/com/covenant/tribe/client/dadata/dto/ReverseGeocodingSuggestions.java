package com.covenant.tribe.client.dadata.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReverseGeocodingSuggestions {

    String value;

    @JsonProperty(value = "unrestricted_value")
    String unrestrictedValue;

    ReverseGeocodingData data;

}
