package com.covenant.tribe.util.querydsl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFilter implements Serializable {

    List<Long> eventTypeId;

    Double distanceInMeters;

    Double longitude;

    Double latitude;

    LocalDate startDate;

    LocalDate endDate;

    Integer numberOfParticipantsMin;

    Integer numberOfParticipantsMax;

    @Schema(example = "MORNING")
    String partsOfDay;

    Integer durationEventInHoursMin;

    Integer durationEventInHoursMax;

    Boolean isPresenceOfAlcohol;

    Boolean isFree;

    Boolean isEighteenYearLimit;
}
