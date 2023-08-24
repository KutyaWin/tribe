package com.covenant.tribe.service.facade.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoCoordsDto;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.external.ExternalEventAddressDto;
import com.covenant.tribe.dto.event.external.ParsedAddressDto;
import com.covenant.tribe.exeption.NotFoundException;
import com.covenant.tribe.repository.EventAddressRepository;
import com.covenant.tribe.service.ParseAddressService;
import com.covenant.tribe.service.ReverseGeolocationService;
import com.covenant.tribe.service.SearchCoordinatesService;
import com.covenant.tribe.service.facade.ExternalEventAddressHandler;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExternalEventAddressHandlerImpl implements ExternalEventAddressHandler {

    EventAddressRepository eventAddressRepository;
    ReverseGeolocationService reverseGeolocationService;
    EventAddressMapper eventAddressMapper;
    ParseAddressService parseAddressService;
    SearchCoordinatesService searchCoordinatesService;

    @Override
    public ExternalEventAddressDto handleExternalEventAddress(KudagoEventDto kudagoEventDto) {
        KudagoCoordsDto kudagoCoordsDto = kudagoEventDto.getPlace().getCoords();
        ExternalEventAddressDto externalEventAddressDto;

        if (isAddressExistInDb(kudagoCoordsDto.getLat(), kudagoCoordsDto.getLat())) {
            externalEventAddressDto = ExternalEventAddressDto.builder()
                    .isAddressExistInDb(true)
                    .build();
            return externalEventAddressDto;
        }

        externalEventAddressDto = getAddressByReverseGeocoding(kudagoEventDto);
        if (externalEventAddressDto != null) {
            return externalEventAddressDto;
        }
        return getAddressByGeocoding(kudagoEventDto);
    }

    private boolean isAddressExistInDb(Double latitude, Double longitude) {
        return eventAddressRepository.existsByEventLongitudeAndEventLatitude(
                longitude, latitude
        );
    }

    private ExternalEventAddressDto getAddressByReverseGeocoding(KudagoEventDto eventDto) {
        ExternalEventAddressDto externalEventAddressDto = null;
        ReverseGeocodingData externalEventAddress = reverseGeolocationService.getExternalEventAddress(eventDto);
        if (externalEventAddress != null) {
            externalEventAddressDto = ExternalEventAddressDto.builder()
                    .isAddressExistInDb(false)
                    .eventAddressDTO(eventAddressMapper.mapToEventAddressDto(externalEventAddress))
                    .build();
        }
        return externalEventAddressDto;
    }

    private ExternalEventAddressDto getAddressByGeocoding(KudagoEventDto eventDto) {
        ParsedAddressDto parsedAddressDto = parseAddressService.parseAddress(eventDto);
        if (parsedAddressDto == null) {
            return null;
        }
        try {
            return searchCoordinatesService
                    .searchCoordinatesByAddress(parsedAddressDto);
        } catch (NotFoundException e) {
            return null;
        }
    }
}
