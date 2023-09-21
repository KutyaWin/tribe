package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventTypeInfoDto implements Serializable {

    Long id;

    @JsonProperty(value = "type_name")
    String typeName;

}