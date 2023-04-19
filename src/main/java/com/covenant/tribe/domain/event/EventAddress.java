package com.covenant.tribe.domain.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "event_addresses")
public class EventAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "event_latitude", nullable = false)
    Double eventLatitude;

    @Column(name = "event_longitude", nullable = false)
    Double eventLongitude;

    @Column(name = "event_position", columnDefinition = "geometry(Point,4326)", nullable = false)
    Point eventPosition;

    @Column(length = 100, nullable = false)
    String city;

    @Column(length = 100)
    String region;

    @Column(length = 100)
    String street;

    @Column(length = 100)
    String district;

    @Column(length = 10)
    String building;

    @Column(name = "house_number", length = 10)
    String houseNumber;

    @Column(length = 10)
    String floor;

    @OneToMany(mappedBy = "eventAddress", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Setter(AccessLevel.PRIVATE)
    List<Event> listEvents;

    static final int SRID = 4326;
    static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID);

    public EventAddress(Long id, Double eventLatitude, Double eventLongitude, String city, String region, String street,
                        String district, String building, String houseNumber, String floor) {
        this.id = id;
        this.eventLatitude = eventLatitude;
        this.eventLongitude = eventLongitude;
        this.city = city;
        this.region = region;
        this.street = street;
        this.district = district;
        this.building = building;
        this.houseNumber = houseNumber;
        this.floor = floor;
        this.eventPosition = geometryFactory.createPoint(new Coordinate(eventLongitude, eventLatitude));
    }

    public void addEvent(Event event) {
        if (this.listEvents == null) this.listEvents = new ArrayList<>();

        if (!this.listEvents.contains(event)) {
            this.listEvents.add(event);
            event.setEventAddress(this);
        } else {
            log.error(
                    String.format("EventAddress already has passed event." +
                            "listEvent: %s. Passed event: %s",
                            this.listEvents.stream().map(Event::getId).toList(), event.getId())
            );
            throw new IllegalArgumentException(
                    String.format("EventAddress already has passed event." +
                                    "listEvent: %s. Passed event: %s",
                            this.listEvents.stream().map(Event::getId).toList(), event.getId())
            );
        }
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        EventAddress eventaddress = (EventAddress) o;
        return this.id != null && this.id.equals(eventaddress.id);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}
