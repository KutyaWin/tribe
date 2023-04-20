package com.covenant.tribe.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.UniqueElements;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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