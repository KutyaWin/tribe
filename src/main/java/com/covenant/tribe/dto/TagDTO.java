package com.covenant.tribe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagDTO implements Serializable {

    Long id;

    @JsonProperty("tag_name")
    @Size(max = 50, message = "tag_name must not consist of more than 50 characters")
    String tagName;
}
