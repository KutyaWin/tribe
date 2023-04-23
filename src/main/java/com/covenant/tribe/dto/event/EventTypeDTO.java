package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventTypeDTO implements Serializable {

    Long id;

    @JsonProperty(value = "type_name")
    @NotBlank(message = "type_name should not be null or empty")
    @Size(max = 50)
    String typeName;

    String animationJson;
}