package com.covenant.tribe.client.dadata.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ReverseGeocodingRequest {

    int count;
    Double lat;
    Double lon;

}
