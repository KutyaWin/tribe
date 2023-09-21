package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.external.ParsedAddressDto;
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
    public EventAddressDTO mapToEventAddressDto(ParsedAddressDto parsedAddressDto, KudagoEventDto kudagoEventDto) {
        EventAddressDTO address = EventAddressDTO.builder()
                .eventLatitude(kudagoEventDto.getPlace().getCoords().getLat())
                .eventLongitude(kudagoEventDto.getPlace().getCoords().getLon())
                .city(parsedAddressDto.getCity())
                .street(parsedAddressDto.getStreet())
                .houseNumber(parsedAddressDto.getHouseNumber())
                .build();
        if (parsedAddressDto.getBuilding() != null) {
            address.setBuilding(parsedAddressDto.getBuilding());
        }
        if (parsedAddressDto.getConstruction() != null) {
            address.setBuilding(parsedAddressDto.getConstruction());
        }
        return address;
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
    public EventAddress mapToEventAddress(Map<Long, EventAddressDTO> reverseGeocodingData, Long currentEventId) {
        EventAddressDTO eventAddressDTO = reverseGeocodingData.get(currentEventId);
        return new EventAddress(
                null,
                eventAddressDTO.getEventLatitude(),
                eventAddressDTO.getEventLongitude(),
                eventAddressDTO.getCity(),
                eventAddressDTO.getRegion(),
                eventAddressDTO.getStreet(),
                eventAddressDTO.getDistrict(),
                eventAddressDTO.getBuilding(),
                eventAddressDTO.getHouseNumber(),
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
