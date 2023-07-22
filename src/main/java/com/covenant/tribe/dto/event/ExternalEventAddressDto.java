package com.covenant.tribe.dto.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalEventAddressDto {

    Double latitude;
    Double longitude;
    String city;
    String region;
    String street;
    String district;
    String building;
    String houseNumber;

}
