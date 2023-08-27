package com.covenant.tribe.client.nominatim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NominatimSearchParams {

    String city;

    String street;

    @Builder.Default
    String format = "json";

    @JsonProperty(value = "address_details")
    @Builder.Default
    int addressDetails = 1;

    @JsonProperty(value = "accept_language")
    @Builder.Default
    String acceptLanguage = "ru";

    @Builder.Default
    int limit = 1;

}
