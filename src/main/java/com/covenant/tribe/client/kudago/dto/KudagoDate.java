package com.covenant.tribe.client.kudago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KudagoDate {
    @JsonProperty("start_date")
    String startDate;
    @JsonProperty("start_time")
    String startTime;
    Long start;
    @JsonProperty("end_date")
    String endDate;
    @JsonProperty("end_time")
    String endTime;
    Long end;
    @JsonProperty("is_continuous")
    Boolean isContinuous;
    @JsonProperty("is_endless")
    Boolean isEndless;
    @JsonProperty("is_startless")
    Boolean isStartless;
    ArrayList<Object> schedules;
    @JsonProperty("use_place_schedule")
    Boolean usePlaceSchedule;
}
