package com.covenant.tribe.service.facade.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoCoordsDto;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.external.ExternalEventAddressDto;
import com.covenant.tribe.repository.EventAddressRepository;
import com.covenant.tribe.service.ReverseGeolocationService;
import com.covenant.tribe.service.facade.ExternalEventAddressHandler;
import com.covenant.tribe.util.mapper.EventAddressMapper;
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
    ReverseGeolocationService reverseGeolocationService;
    EventAddressMapper eventAddressMapper;

    @Override
    public ExternalEventAddressDto handleExternalEventAddress(KudagoEventDto kudagoEventDto) {
        KudagoCoordsDto kudagoCoordsDto = kudagoEventDto.getPlace().getCoords();
        ExternalEventAddressDto externalEventAddressDto;

        if (isAddressExistInDb(kudagoCoordsDto.getLat(), kudagoCoordsDto.getLat())) {
            externalEventAddressDto = ExternalEventAddressDto.builder()
                    .isEventExistInDb(true)
                    .build();
            return externalEventAddressDto;
        }

        externalEventAddressDto = tryToGetAddressFromReverseGeocoding(kudagoEventDto);
        if (externalEventAddressDto != null) {
            return externalEventAddressDto;
        }



    }

    private boolean isAddressExistInDb(Double latitude, Double longitude) {
        return eventAddressRepository.existsByEventLongitudeAndEventLatitude(
                longitude, latitude
        );
    }

    private ExternalEventAddressDto tryToGetAddressFromReverseGeocoding(KudagoEventDto eventDto) {
        ExternalEventAddressDto externalEventAddressDto = null;
        ReverseGeocodingData externalEventAddress = reverseGeolocationService.getExternalEventAddress(eventDto);
        if (externalEventAddress != null) {
            externalEventAddressDto = ExternalEventAddressDto.builder()
                    .isEventExistInDb(false)
                    .eventAddressDTO(eventAddressMapper.mapToEventAddressDto(externalEventAddress))
                    .build();
        }
        return externalEventAddressDto;
    }


}
