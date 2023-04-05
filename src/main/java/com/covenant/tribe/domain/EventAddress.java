package com.covenant.tribe.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.persistence.*;
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event_addresses")
public class EventAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "latitude", nullable = false, precision = 15)
    Double latitude;

    @Column(name = "longitude", nullable = false, precision = 15)
    Double longitude;

    @OneToOne
    @JoinColumn(name = "city_id", nullable = false)
    City city;

    @Column(name = "street", length = 100)
    String street;

    @Column(name = "house", length = 10)
    String house;

    @Column(name = "building", length = 10)
    String building;

    @Column(name = "floor", length = 3)
    String floor;

}
