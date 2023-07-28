package com.covenant.tribe.util.mapper;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.dto.event.EventAddressDTO;

import java.util.Map;

public interface EventAddressMapper {

    EventAddressDTO mapToEventAddressDTO(EventAddress eventAddress);

    EventAddress mapToEventAddress(EventAddressDTO dto);

    EventAddress matToEventAddress(
            Map<Long, ReverseGeocodingData> reverseGeocodingData,
            Long currentEventId
    );
}
