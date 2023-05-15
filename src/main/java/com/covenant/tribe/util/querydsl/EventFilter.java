package com.covenant.tribe.util.querydsl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    String partsOfDay;

    Integer durationEventInHoursMin;

    Integer durationEventInHoursMax;

    Boolean isPresenceOfAlcohol;

    Boolean isFree;

    Boolean isEighteenYearLimit;
}
