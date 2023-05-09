package com.covenant.tribe.util.querydsl;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFilter {

    @JsonProperty("event_type_id")
    List<Long> eventTypeIdList;

    @JsonProperty("distance_in_meters")
    double distanceInMeters;

    @JsonProperty("start_date")
    LocalDate startDate;

    @JsonProperty("end_date")
    LocalDate endDate;

    @JsonProperty("number_of_participants_min")
    Long numberOfParticipantsMin;

    @JsonProperty("number_of_participants_max")
    Long numberOfParticipantsMax;

    @JsonProperty("parts_of_day")
    @Schema(example = "EVENING")
    String partsOfDay;

    @JsonProperty("is_presence_of_alcohol")
    boolean isPresenceOfAlcohol;

    @JsonProperty("is_free")
    boolean isFree;

    @JsonProperty("is_eighteen_year_limit")
    boolean isEighteenYearLimit;
}
