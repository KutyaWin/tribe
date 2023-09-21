package com.covenant.tribe.util.mapper;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.external.ParsedAddressDto;

import java.util.Map;

public interface EventAddressMapper {

    EventAddressDTO mapToEventAddressDto(EventAddress eventAddress);

    EventAddressDTO mapToEventAddressDto(ParsedAddressDto parsedAddressDto, KudagoEventDto kudagoEventDto);

    EventAddress mapToEventAddress(EventAddressDTO dto);

    EventAddress mapToEventAddress(
            Map<Long, EventAddressDTO> reverseGeocodingData,
            Long currentEventId
    );

    EventAddressDTO mapToEventAddressDto(ReverseGeocodingData reverseGeocodingData);
}
