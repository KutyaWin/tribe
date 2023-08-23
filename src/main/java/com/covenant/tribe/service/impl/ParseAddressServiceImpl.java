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
        String address = kudagoEventDto.getLocation().getName() + ", " + kudagoEventDto.getPlace().getAddress();
        TextAddress textAddress = AddressService.processSingleAddressText(address, null);
        ParsedAddressDto parsedAddressDto = new ParsedAddressDto();
        textAddress.items.forEach(item -> {

            if (item.level.equals(AddrLevel.CITY)) {
                AreaAttributes areaAttributes = (AreaAttributes) item.attrs;
                parsedAddressDto.setCity(areaAttributes.names.get(0));
            }

            if (item.level.equals(AddrLevel.STREET)) {
                AreaAttributes areaAttributes = (AreaAttributes) item.attrs;
                parsedAddressDto.setStreet(areaAttributes.names.get(0));
            }

            if (item.level.equals(AddrLevel.BUILDING)) {
                HouseAttributes houseAttributes = (HouseAttributes) item.attrs;
                parsedAddressDto.setHouseNumber(houseAttributes.number);
                parsedAddressDto.setBuilding(houseAttributes.buildNumber);
                parsedAddressDto.setConstruction(houseAttributes.stroenNumber);
            }

        });
        return parsedAddressDto;
    }
}
