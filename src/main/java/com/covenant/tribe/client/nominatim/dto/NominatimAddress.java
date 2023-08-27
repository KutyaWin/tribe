package com.covenant.tribe.client.nominatim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
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
