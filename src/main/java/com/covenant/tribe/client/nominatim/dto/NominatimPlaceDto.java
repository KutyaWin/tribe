package com.covenant.tribe.client.nominatim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class NominatimPlaceDto {

    @JsonProperty(value = "place_id")
    String placeId;

    String licence;

    @JsonProperty(value = "osm_type")
    String osmType;

    @JsonProperty( value = "osm_id")
    String osmId;

    @JsonProperty(value = "bounding_box")
    List<String> boundingBox;

    Double lat;

    Double lon;

    @JsonProperty(value = "display_name")
    String displayName;

    @JsonProperty(value = "class")
    String placeClass;

    String type;

    double importance;

    String icon;

    NominatimAddress address;

    @JsonProperty(value = "extra_tags")
    Map<String, String> extraTags;

}
