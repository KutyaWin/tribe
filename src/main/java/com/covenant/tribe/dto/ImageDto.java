package com.covenant.tribe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDto {

    @JsonProperty(value = "content_type")
    @Schema(example = "image/jpg")
    String contentType;

    @Schema(example = "[25, 10, -50, ...]")
    byte[] image;

}
