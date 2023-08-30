package com.covenant.tribe.client.kudago.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudagoEventDto {
    Long id;
    @JsonProperty("publication_date")
    Long publicationDate;
    List<KudagoDate> dates;
    String title;
    @JsonProperty("short_title")
    String shortTitle;
    String slug;
    KudagoPlace place;
    String description;
    @JsonProperty("body_text")
    String bodyText;
    KudagoLocationDto location;
    List<String> categories;
    String tagline;
    @JsonProperty("age_restriction")
    String ageRestriction;
    String price;
    @JsonProperty("is_free")
    Boolean isFree;
    List<KudagoImageDto> images;
    @JsonProperty("favourites_count")
    Long favouritesCount;
    @JsonProperty("comments_count")
    Long commentsCount;
    @JsonProperty("site_url")
    String siteUrl;
    List<String> tags;
//    String participants;
}
