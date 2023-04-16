package com.covenant.tribe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ImageDTO {

    @JsonProperty(value = "content_type")
    String contentType;

    byte[] image;

}
