package com.covenant.tribe.client.kudago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudagoPlace {

    Integer id;
    String title;
    String slug;
    String address;
    String phone;
    @JsonProperty("is_stub")
    Boolean isStub;
    @JsonProperty("site_url")
    String siteUrl;
    KudagoCoordsDto coords;
    String subway;
    @JsonProperty("is_closed")
    Boolean isClosed;
    String location;
}
