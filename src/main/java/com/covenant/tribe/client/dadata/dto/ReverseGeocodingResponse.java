package com.covenant.tribe.client.dadata.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReverseGeocodingResponse {

    List<ReverseGeocodingSuggestions> suggestions;

}
