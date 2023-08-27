package com.covenant.tribe.service.facade.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoCoordsDto;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.external.ExternalEventAddressDto;
import com.covenant.tribe.dto.event.external.ParsedAddressDto;
import com.covenant.tribe.repository.EventAddressRepository;
import com.covenant.tribe.service.ParseAddressService;
import com.covenant.tribe.service.ReverseGeolocationService;
import com.covenant.tribe.service.SearchCoordinatesService;
import com.covenant.tribe.service.facade.ExternalEventAddressHandler;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ExternalEventAddressHandlerImpl implements ExternalEventAddressHandler {

    EventAddressRepository eventAddressRepository;
    ReverseGeolocationService reverseGeolocationService;
    EventAddressMapper eventAddressMapper;
    ParseAddressService parseAddressService;

    @Override
    public EventAddressDTO handleExternalEventAddress(KudagoEventDto kudagoEventDto) {
        EventAddressDTO eventAddressDTO;

        eventAddressDTO = getAddressByReverseGeocoding(kudagoEventDto);
        if (eventAddressDTO != null && eventAddressDTO.getCity() != null) {
            return eventAddressDTO;
        }
        ParsedAddressDto parsedAddress = getParsedAddress(kudagoEventDto);
        if (parsedAddress == null) {
            return null;
        } else {
            eventAddressDTO = eventAddressMapper.mapToEventAddressDto(
                parsedAddress, kudagoEventDto
            );
            if (eventAddressDTO.getCity() == null && eventAddressDTO.getEventLatitude() == null
            || eventAddressDTO.getEventLongitude() == null) {
                String erMessage = "Address %s is not valid".formatted(kudagoEventDto);
                log.error(erMessage);
                return null;
            }
            return eventAddressDTO;
        }
    }

    private boolean isAddressExistInDb(Double latitude, Double longitude) {
        return eventAddressRepository.existsByEventLongitudeAndEventLatitude(
                longitude, latitude
        );
    }

    private EventAddressDTO getAddressByReverseGeocoding(KudagoEventDto eventDto) {
        EventAddressDTO eventAddressDto = null;
        ReverseGeocodingData externalEventAddress = reverseGeolocationService.getExternalEventAddress(eventDto);
        if (externalEventAddress != null) {
            eventAddressDto = eventAddressMapper.mapToEventAddressDto(externalEventAddress);
        }
        return eventAddressDto;
    }

    private ParsedAddressDto getParsedAddress(KudagoEventDto eventDto) {
        return parseAddressService.parseAddress(eventDto);
    }
}
