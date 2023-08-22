package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class EventAddressMapperImpl implements EventAddressMapper {

    @Override
    public EventAddressDTO mapToEventAddressDto(EventAddress eventAddress) {

        return EventAddressDTO.builder()
                .eventLatitude(eventAddress.getEventLatitude())
                .eventLongitude(eventAddress.getEventLongitude())
                .city(eventAddress.getCity())
                .region(eventAddress.getRegion())
                .street(eventAddress.getStreet())
                .district(eventAddress.getDistrict())
                .building(eventAddress.getBuilding())
                .houseNumber(eventAddress.getHouseNumber())
                .floor(eventAddress.getFloor())
                .build();
    }

    @Override
    public EventAddress mapToEventAddress(EventAddressDTO dto) {

        return new EventAddress(
                null,
                dto.getEventLatitude(),
                dto.getEventLongitude(),
                dto.getCity(),
                dto.getRegion(),
                dto.getStreet(),
                dto.getDistrict(),
                dto.getBuilding(),
                dto.getHouseNumber(),
                dto.getFloor());
    }

    @Override
    public EventAddress mapToEventAddress(Map<Long, ReverseGeocodingData> reverseGeocodingData, Long currentEventId) {
        ReverseGeocodingData geocodingDataForCurrentEvent = reverseGeocodingData.get(currentEventId);
        return new EventAddress(
                null,
                Double.valueOf(geocodingDataForCurrentEvent.getGeoLat()),
                Double.valueOf(geocodingDataForCurrentEvent.getGeoLon()),
                geocodingDataForCurrentEvent.getCity(),
                geocodingDataForCurrentEvent.getRegionWithType(),
                geocodingDataForCurrentEvent.getStreet(),
                geocodingDataForCurrentEvent.getCityDistrict(),
                geocodingDataForCurrentEvent.getBlock(),
                geocodingDataForCurrentEvent.getHouse(),
                null
        );
    }

    @Override
    public EventAddressDTO mapToEventAddressDto(ReverseGeocodingData reverseGeocodingData) {
        return EventAddressDTO.builder()
                .eventLatitude(Double.valueOf(reverseGeocodingData.getGeoLat()))
                .eventLongitude(Double.valueOf(reverseGeocodingData.getGeoLon()))
                .district(reverseGeocodingData.getCityDistrict())
                .houseNumber(reverseGeocodingData.getHouse())
                .street(reverseGeocodingData.getStreet())
                .city(reverseGeocodingData.getCity())
                .building(reverseGeocodingData.getBlock())
                .region(reverseGeocodingData.getRegionWithType())
                .build();
    }
}
