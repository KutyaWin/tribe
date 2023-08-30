package com.covenant.tribe.service;

import com.covenant.tribe.dto.event.external.ExternalEventAddressDto;
import com.covenant.tribe.dto.event.external.ParsedAddressDto;

public interface SearchCoordinatesService {

    ExternalEventAddressDto searchCoordinatesByAddress(ParsedAddressDto address);

}
