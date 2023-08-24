package com.covenant.tribe.client.nominatim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class NominatimAddress {

    String city;

    @JsonProperty(value = "city_district")
    String cityDistrict;

    String construction;

    String continent;

    String country;

    @JsonProperty(value = "country_code")
    String countryCode;

    @JsonProperty(value = "house_number")
    String houseNumber;

    String neighbourhood;
    String postcode;

    @JsonProperty(value = "public_building")
    String publicBuilding;

    String state;

    String suburb;

}
