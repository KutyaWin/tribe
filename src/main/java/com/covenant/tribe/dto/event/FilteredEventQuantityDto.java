package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FilteredEventQuantityDto {
    @JsonProperty(value = "event_quantity")
    Long eventQuantity;
}
