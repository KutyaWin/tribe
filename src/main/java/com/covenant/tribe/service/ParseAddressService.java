package com.covenant.tribe.service;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.external.ParsedAddressDto;

public interface ParseAddressService {

    ParsedAddressDto parseAddress(KudagoEventDto kudagoEventDto);

}
