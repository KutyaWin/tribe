package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventAddressRepository extends JpaRepository<EventAddress, Long> {

    boolean existsByEventLongitudeAndEventLatitude(Double longitude, Double latitude);

    EventAddress findByEventLatitudeAndEventLongitudeAndHouseNumberAndBuilding(
            Double latitude, Double longitude, String houseNumber, String building
    );

}
