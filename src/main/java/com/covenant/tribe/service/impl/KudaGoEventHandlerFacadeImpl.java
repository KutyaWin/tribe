package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoClientParams;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.EventComparisonDto;
import com.covenant.tribe.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class KudaGoEventHandlerFacadeImpl implements ExternalEventHandlerFacade {


    KudagoFetchService kudagoFetchService;
    ExternalEventService externalEventService;
    EventService eventService;
    CompareEventService compareEventService;


    @Override
    public void handleNewEvents(Map<Long, KudagoEventDto> externalEvents) {
        OffsetDateTime createEventTime = OffsetDateTime.now().minusHours(1);
        Optional<Map<Long, KudagoEventDto>> kudaGoEventsOpt = Optional.empty();
        try {
            kudaGoEventsOpt = Optional.of(kudagoFetchService
                    .fetchPosts(new KudagoClientParams(createEventTime)));
        } catch (JsonProcessingException e) {
            String erMessage = "[EXCEPTION]: Failed to process data from KudaGo. Error: %s".
                    formatted(e.getMessage());
            log.error(erMessage);
        }

        List<KudagoEventDto> eventsForComparing = null;
        if (kudaGoEventsOpt.isPresent()) {
            Map<Long, KudagoEventDto> kudaGoEvents = kudaGoEventsOpt.get();
            eventsForComparing = externalEventService.deleteExistingInDbEvents(kudaGoEvents);
        }

        List<EventComparisonDto> eventComparisons = eventService.getEventComparisonDto();


    }
}
