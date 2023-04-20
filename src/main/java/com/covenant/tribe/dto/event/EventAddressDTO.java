package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventAddressDTO implements Serializable {

    @JsonProperty("event_latitude")
    Double eventLatitude;

    @JsonProperty("event_longitude")
    Double eventLongitude;

    @Size(max = 100, message = "city must not consist of more than 100 characters")
    String city;

    @Size(max = 100, message = "region must not consist of more than 100 characters")
    String region;

    @Size(max = 100, message = "street must not consist of more than 100 characters")
    String street;

    @Size(max = 100, message = "district must not consist of more than 100 characters")
    String district;

    @Size(max = 10, message = "building must not consist of more than 10 characters")
    String building;

    @JsonProperty("house_number")
    @Size(max = 10, message = "house_number must not consist of more than 10 characters")
    String houseNumber;

    @Size(max = 10, message = "floor must not consist of more than 10 characters")
    String floor;
}
