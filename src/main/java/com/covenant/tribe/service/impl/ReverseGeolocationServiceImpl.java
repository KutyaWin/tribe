package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dadata.DaDataClient;
import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.dadata.dto.ReverseGeocodingRequest;
import com.covenant.tribe.client.dadata.dto.ReverseGeocodingResponse;
import com.covenant.tribe.client.dadata.dto.ReverseGeocodingSuggestions;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.service.ReverseGeolocationService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
            Double latitude = event.getLocation().coords.getLat();
            Double longitude = event.getLocation().coords.getLon();
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
                ReverseGeocodingData address = suggestions.get(0).getData();
                externalEventAddresses.put(event.getId(), address);
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        });
        return externalEventAddresses;
    }
}
