package com.covenant.tribe.domain.event.search;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.locationtech.jts.geom.Point;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventAddressSearchUnit {

    Long id;

    Double eventLatitude;

    Double eventLongitude;

    List<String> names;

    Point eventPosition;
}