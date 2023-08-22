package com.covenant.tribe.service.facade.impl;

import com.covenant.tribe.client.kudago.dto.KudagoCoordsDto;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.external.ExternalEventAddressDto;
import com.covenant.tribe.repository.EventAddressRepository;
import com.covenant.tribe.service.facade.ExternalEventAddressHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExternalEventAddressHandlerImpl implements ExternalEventAddressHandler {

    EventAddressRepository eventAddressRepository;

    @Override
    public ExternalEventAddressDto handleExternalEventAddress(KudagoEventDto kudagoEventDto) {
        Map<Long, ExternalEventAddressDto> externalEventAddresses = new HashMap<>();
        KudagoCoordsDto kudagoCoordsDto = kudagoEventDto.getPlace().getCoords();
        ExternalEventAddressDto externalEventAddressDto;
        if (isAddressExistInDb(kudagoCoordsDto.getLat(), kudagoCoordsDto.getLat())) {
            externalEventAddressDto = ExternalEventAddressDto.builder()
                    .isEventExistInDb(true)
                    .build();
            return externalEventAddressDto;
        }
    }

    private boolean isAddressExistInDb(Double latitude, Double longitude) {
        return eventAddressRepository.existsByEventLongitudeAndEventLatitude(
                longitude, latitude
        );
    }
}
