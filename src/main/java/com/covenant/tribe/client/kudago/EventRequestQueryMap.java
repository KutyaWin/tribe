package com.covenant.tribe.client.kudago;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventRequestQueryMap {

    String lang;

    Integer page;

    Integer page_size;

    String fields;

    String expand;

    String order_by;

    String text_format;

    String ids;

    String location;

    Long actual_since;

    Long actual_until;

    String place_id;

    String parent_id;

    Boolean is_free;

    String categories;

    String tags;

    Long lon;

    Long lat;

    Long radius;
}
