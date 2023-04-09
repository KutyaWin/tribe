package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnknownUserWithInterestsDTO implements Serializable {
    @JsonProperty(value = "bluetooth_id")
    String bluetoothId;
    @JsonProperty(value = "event_type_ids")
    List<Long> eventTypeIds;
}
