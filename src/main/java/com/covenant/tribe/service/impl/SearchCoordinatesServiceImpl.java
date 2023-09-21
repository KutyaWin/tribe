package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.nominatim.NominatimClient;
import com.covenant.tribe.client.nominatim.dto.NominatimAddress;
import com.covenant.tribe.client.nominatim.dto.NominatimPlaceDto;
import com.covenant.tribe.client.nominatim.dto.NominatimSearchParams;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.external.ExternalEventAddressDto;
import com.covenant.tribe.dto.event.external.ParsedAddressDto;
import com.covenant.tribe.exeption.NotFoundException;
import com.covenant.tribe.service.SearchCoordinatesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Service
public class SearchCoordinatesServiceImpl implements SearchCoordinatesService {

    NominatimClient nominatimClient;

    @Override
    public ExternalEventAddressDto searchCoordinatesByAddress(ParsedAddressDto address) {
        StringBuilder streetWithHouseNumber = new StringBuilder()
                .append(address.getStreet())
                .append(" ")
                .append(address.getHouseNumber())
                .append(" ");
        if (address.getBuilding() != null) {
            streetWithHouseNumber
                    .append("к")
                    .append(address.getStreet());
        }
        if (address.getConstruction() != null) {
            streetWithHouseNumber
                    .append("c")
                    .append(address.getConstruction());
        }
        NominatimSearchParams nominatimSearchParams = NominatimSearchParams.builder()
                .city(address.getCity())
                .street(streetWithHouseNumber.toString())
                .build();
        ResponseEntity<NominatimPlaceDto> addressFromGeocoding =
                nominatimClient.getAddressFromGeocoding(nominatimSearchParams);
        if (addressFromGeocoding.getStatusCode().is2xxSuccessful()
                && addressFromGeocoding.getBody() != null
        ) {
            NominatimPlaceDto nominatimPlaceDto = addressFromGeocoding.getBody();
            NominatimAddress nominatimAddress = nominatimPlaceDto.getAddress();
            if (nominatimAddress == null) {
                String erMessage = "Nominatim can't find address with params: %s".formatted(
                        nominatimSearchParams
                );
                log.error(erMessage);
                throw new NotFoundException(erMessage);
            }
            EventAddressDTO addressDTO = EventAddressDTO.builder()
                    .city(nominatimAddress.getCity())
                    .street(address.getStreet())
                    .houseNumber(address.getHouseNumber())
                    .region(nominatimAddress.getState())
                    .eventLatitude(nominatimPlaceDto.getLat())
                    .eventLongitude(nominatimPlaceDto.getLon())
                    .build();
            if (address.getBuilding() != null) {
                addressDTO.setBuilding(address.getBuilding());
            } else {
                addressDTO.setBuilding(address.getConstruction());
            }
            return ExternalEventAddressDto.builder()
                    .eventAddressDTO(addressDTO)
                    .isAddressExistInDb(false)
                    .build();
        }
        return null;
    }
}
