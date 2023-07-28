package com.covenant.tribe.domain.event.search;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventAddressSearchUnit {

    private Long id;

    private Double eventLatitude;

    private Double eventLongitude;

    private List<String> names;

    private GeoPoint eventPosition;

    public static EventAddressSearchUnitBuilder builder() {
        return new EventAddressSearchUnitBuilder();
    }

    public static class EventAddressSearchUnitBuilder {
        private Long id;
        private Double eventLatitude;
        private Double eventLongitude;
        private List<String> names;
        private GeoPoint eventPosition;

        EventAddressSearchUnitBuilder() {
        }

        public EventAddressSearchUnitBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EventAddressSearchUnitBuilder eventLatitude(Double eventLatitude) {
            this.eventLatitude = eventLatitude;
            return this;
        }

        public EventAddressSearchUnitBuilder eventLongitude(Double eventLongitude) {
            this.eventLongitude = eventLongitude;
            return this;
        }

        public EventAddressSearchUnitBuilder names(List<String> names) {
            this.names = names;
            return this;
        }

        public EventAddressSearchUnitBuilder eventPosition(GeoPoint eventPosition) {
            this.eventPosition = eventPosition;
            return this;
        }

        public EventAddressSearchUnit build() {
            if (eventPosition == null) eventPosition = new GeoPoint(eventLatitude, eventLongitude);
            return new EventAddressSearchUnit(this.id, this.eventLatitude, this.eventLongitude, this.names, this.eventPosition);
        }

        public String toString() {
            return "EventAddressSearchUnit.EventAddressSearchUnitBuilder(id=" + this.id + ", eventLatitude=" + this.eventLatitude + ", eventLongitude=" + this.eventLongitude + ", names=" + this.names + ", eventPosition=" + this.eventPosition + ")";
        }
    }
}