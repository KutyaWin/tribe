package com.covenant.tribe.service;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.ExternalEventAddressDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ReverseGeolocationService {

    Map<Long, ReverseGeocodingData> getExternalEventAddresses(List<KudagoEventDto> events);

}
