package com.covenant.tribe.service;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ExternalEventService {
    List<KudagoEventDto> prepareEventsForCreating(
            Map<Long, KudagoEventDto> kudaGoEvents,
            int daysQuantityToFirstPublication
    );

    void saveNewExternalEvents(
            List<KudagoEventDto> kudaGoEvents,
            Map<Long, ReverseGeocodingData> reverseGeocodingData,
            Map<Long, List<String>> imageFileNames,
            Map<Long, List<Long>> eventTagIds,
            Map<Long, ExternalEventDates> externalEventDates
    );

}
