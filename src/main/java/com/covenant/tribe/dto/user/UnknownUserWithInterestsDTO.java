package com.covenant.tribe.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
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

    @JsonProperty("firebase_id")
    @Size(max = 100, message = "firebase_id must not consist of more than 100 characters")
    String firebaseId;

    @JsonProperty(value = "event_type_ids")
    List<Long> eventTypeIds;
}
