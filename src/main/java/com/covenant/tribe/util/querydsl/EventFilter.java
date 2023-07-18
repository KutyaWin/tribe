package com.covenant.tribe.util.querydsl;

import com.covenant.tribe.service.impl.EventSort;
import com.covenant.tribe.service.impl.SortOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class EventFilter implements Serializable {

    String text;

    List<Long> eventTypeId;

    Double distanceInMeters;

    Double longitude;

    Double latitude;

    LocalDate startDate;

    LocalDate endDate;

    Integer numberOfParticipantsMin;

    Integer numberOfParticipantsMax;

    @Schema(example = "MORNING,EVENING,NIGHT")
    String partsOfDay;

    Integer durationEventInHoursMin;

    Integer durationEventInHoursMax;

    Boolean isPresenceOfAlcohol;

    Boolean isFree;

    Boolean isEighteenYearLimit;

    @Schema(description = "Available: DISTANCE, DATE, ALCOHOL")
    EventSort sort;

    @Builder.Default
    Boolean strictEventSort = false;

    @Schema(description = "Can be: ASC, DSC")
    SortOrder order;
}
