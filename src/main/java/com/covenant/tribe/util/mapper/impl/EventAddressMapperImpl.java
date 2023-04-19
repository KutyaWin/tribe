package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class EventAddressMapperImpl implements EventAddressMapper {

    @Override
    public EventAddressDTO mapToEventAddressDTO(EventAddress eventAddress) {
        log.debug("map EventAddress to EventAddressDTO. Passed eventAddress: {}", eventAddress);

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
        log.debug("map EventAddressDTO to EventAddress. Passed eventAddressDTO: {}", dto);

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
}
