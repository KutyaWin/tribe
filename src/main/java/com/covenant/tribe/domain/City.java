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
@Table(name = "cities")
public class City {

    @Id
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "region", nullable = false)
    String region;

    @Column(name = "latitude", nullable = false, precision = 15)
    Double latitude;

    @Column(name = "longitude", nullable = false, precision = 15)
    Double longitude;

    @Column(name = "country", nullable = false)
    String country;

}
