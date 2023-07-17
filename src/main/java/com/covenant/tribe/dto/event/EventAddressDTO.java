package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventAddressDTO implements Serializable {

    @JsonProperty("event_longitude")
    @NotNull(message = "event_longitude should not be null")
    Double eventLongitude;

    @JsonProperty("event_latitude")
    @NotNull(message = "event_latitude should not be null")
    Double eventLatitude;

    @NotNull(message = "city should not be null or empty")
    @Size(max = 50, message = "city must not consist of more than 100 characters")
    String city;

    @NotNull(message = "region should not be null or empty")
    @Size(max = 50, message = "region must not consist of more than 100 characters")
    String region;

    @NotNull(message = "street should not be null or empty")
    @Size(max = 50, message = "street must not consist of more than 100 characters")
    String street;

    @Size(max = 50, message = "district must not consist of more than 100 characters")
    String district;

    @Size(max = 10, message = "building must not consist of more than 10 characters")
    String building;

    @NotNull(message = "house_number should not be null or empty")
    @JsonProperty("house_number")
    @Size(max = 10, message = "house_number must not consist of more than 10 characters")
    String houseNumber;

    @Size(max = 10, message = "floor must not consist of more than 10 characters")
    String floor;
}
