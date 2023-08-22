package com.covenant.tribe.util.mapper;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.dto.event.EventAddressDTO;

import java.util.Map;

public interface EventAddressMapper {

    EventAddressDTO mapToEventAddressDto(EventAddress eventAddress);

    EventAddress mapToEventAddress(EventAddressDTO dto);

    EventAddress mapToEventAddress(
            Map<Long, ReverseGeocodingData> reverseGeocodingData,
            Long currentEventId
    );

    EventAddressDTO mapToEventAddressDto(ReverseGeocodingData reverseGeocodingData);
}
