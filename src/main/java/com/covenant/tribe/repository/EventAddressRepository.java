package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.EventAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventAddressRepository extends JpaRepository<EventAddress, Long> {

    boolean existsByEventLongitudeAndEventLatitude(Double longitude, Double latitude);

    List<EventAddress> findByEventLatitudeAndEventLongitudeAndHouseNumberAndBuilding(
            Double latitude, Double longitude, String houseNumber, String building
    );

}
