package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.external.ParsedAddressDto;
import com.covenant.tribe.service.ParseAddressService;
import com.pullenti.address.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ParseAddressServiceImpl implements ParseAddressService {

    @PostConstruct
    public void initPullenti() {
        try {
            AddressService.initialize();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public ParsedAddressDto parseAddress(KudagoEventDto kudagoEventDto) {
        String address = kudagoEventDto.getPlace().getAddress();
        TextAddress textAddress = AddressService.processSingleAddressText(address, null);
        ParsedAddressDto parsedAddressDto = new ParsedAddressDto();
        if (textAddress.items.isEmpty()) {
            return null;
        }
        textAddress.items.forEach(item -> {

            if (item.level.equals(AddrLevel.CITY)) {
                AreaAttributes areaAttributes = (AreaAttributes) item.attrs;
                parsedAddressDto.setCity(areaAttributes.names.get(0));
            }

            if (item.level.equals(AddrLevel.TERRITORY)) {
                AreaAttributes areaAttributes = (AreaAttributes) item.attrs;
                StringBuilder street = new StringBuilder();
                if (!areaAttributes.types.isEmpty()) {
                    street.append(areaAttributes.types.get(0))
                            .append(" ");
                }
                if (!areaAttributes.names.isEmpty()) {
                    street.append(areaAttributes.names.get(0));
                }
                parsedAddressDto.setStreet(street.toString());
            }

            if (item.level.equals(AddrLevel.STREET)) {
                AreaAttributes areaAttributes = (AreaAttributes) item.attrs;
                StringBuilder street = new StringBuilder(parsedAddressDto.getStreet());
                if (!areaAttributes.number.isEmpty()) {
                    street.append(" ")
                            .append(areaAttributes.number);
                }
                if (!areaAttributes.types.isEmpty()) {
                    street.append(" ")
                            .append(areaAttributes.types.get(0));
                }
                if (!areaAttributes.names.isEmpty()) {
                    street.append(areaAttributes.names.get(0));
                }
                parsedAddressDto.setStreet(street.toString());
            }

            if (item.level.equals(AddrLevel.BUILDING)) {
                HouseAttributes houseAttributes = (HouseAttributes) item.attrs;
                parsedAddressDto.setHouseNumber(houseAttributes.number);
                parsedAddressDto.setBuilding(houseAttributes.buildNumber);
                parsedAddressDto.setConstruction(houseAttributes.stroenNumber);
            }

        });
        if (parsedAddressDto.getStreet() == null || parsedAddressDto.getHouseNumber() == null) {
            String erMessage = "Cannot parse address: %s".formatted(address);
            return null;
        }
        if (parsedAddressDto.getCity() == null) {
            parsedAddressDto.setCity(kudagoEventDto.getLocation().name);
        }
        return parsedAddressDto;
    }
}
