package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dadata.DaDataClient;
import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.dadata.dto.ReverseGeocodingRequest;
import com.covenant.tribe.client.dadata.dto.ReverseGeocodingResponse;
import com.covenant.tribe.client.dadata.dto.ReverseGeocodingSuggestions;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.service.ReverseGeolocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReverseGeolocationServiceImpl implements ReverseGeolocationService {

    final int DADATA_ADDRESS_SUGGESTIONS_COUNT = 1;

    final DaDataClient daDataClient;

    public ReverseGeolocationServiceImpl(DaDataClient daDataClient) {
        this.daDataClient = daDataClient;
    }

    @Override
    public Map<Long, ReverseGeocodingData> getExternalEventAddresses(
            List<KudagoEventDto> events
    ) {
        Map<Long, ReverseGeocodingData> externalEventAddresses = new HashMap<>();
        events.forEach(event -> {
            ReverseGeocodingData externalEventAddress = getExternalEventAddress(event);
            externalEventAddresses.put(event.getId(), externalEventAddress);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        });
        return externalEventAddresses;
    }

    @Override
    public ReverseGeocodingData getExternalEventAddress(KudagoEventDto event) {
        Double latitude = event.getPlace().getCoords().getLat();
        Double longitude = event.getPlace().getCoords().getLon();
        ReverseGeocodingRequest reverseGeocodingRequest =
                ReverseGeocodingRequest.builder()
                        .count(DADATA_ADDRESS_SUGGESTIONS_COUNT)
                        .lat(latitude)
                        .lon(longitude)
                        .build();
        ResponseEntity<ReverseGeocodingResponse> geolocationDataResponse
                = daDataClient.getGeolocationData(reverseGeocodingRequest);
        if (geolocationDataResponse.getStatusCode().is2xxSuccessful()
                && geolocationDataResponse.getBody() != null) {
            List<ReverseGeocodingSuggestions> suggestions = geolocationDataResponse.getBody().getSuggestions();
            if (suggestions.isEmpty()) {
                log.error("Dadata doesn't know address of event: {}", event.getPlace());
            } else {
                return suggestions.get(0).getData();
            }
        }
        return null;
    }
}
