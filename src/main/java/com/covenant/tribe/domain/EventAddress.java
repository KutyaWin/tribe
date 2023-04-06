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

    @Column(name = "country", length = 100)
    String country;

    @Column(name = "city", nullable = false, length = 100)
    String city;

    @Column(name = "region", length = 100)
    String region;

    @Column(name = "street", length = 100)
    String street;

    @Column(name = "district", length = 100)
    String district;

    @Column(name = "building", length = 10)
    String building;

    @Column(name = "house_number", length = 10)
    String houseNumber ;

    @Column(name = "floor", length = 10)
    String floor;

}
