package com.covenant.tribe.service;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.event.EventContactInfo;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.external.ExternalEventAddressDto;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface ExternalEventService {
    List<KudagoEventDto> prepareEventsForCreating(
            Map<Long, KudagoEventDto> kudaGoEvents, LocalDateTime minPublicationDate
    );

    void saveNewExternalEvents(
            List<KudagoEventDto> kudaGoEvents,
            Map<Long, List<EventContactInfo>> eventContactInfos,
            Map<Long, EventAddressDTO> reverseGeocodingData,
            Map<Long, List<String>> imageFileNames,
            Map<Long, List<Long>> eventTagIds,
            Map<Long, ExternalEventDates> externalEventDates
    );

}
