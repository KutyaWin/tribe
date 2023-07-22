package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoDate;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.repository.KudaGoEventRepository;
import com.covenant.tribe.service.ExternalEventService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalEventServiceImpl implements ExternalEventService {

    KudaGoEventRepository kudaGoRepository;

    @Override
    public List<KudagoEventDto> prepareEventsForCreating(
            Map<Long, KudagoEventDto> kudaGoEvents,
            int daysQuantityToFirstPublication
    ) {
        List<Long> existingEventIds = kudaGoRepository.findAllRepeatableEventIds(
                kudaGoEvents.keySet(), List.of(LocalDate.now(), LocalDate.now().minusDays(2))
        );
        existingEventIds.forEach(kudaGoEvents::remove);
        List<KudagoEventDto> filteredEvents = filterEvents(kudaGoEvents, daysQuantityToFirstPublication);
        return deleteExpiredStartDates(filteredEvents);
    }

    private List<KudagoEventDto> deleteExpiredStartDates(List<KudagoEventDto> filteredEvents) {
        for(KudagoEventDto event : filteredEvents) {
            event.getDates().removeIf(
                    date -> LocalDate.parse(date.getStartDate()).isBefore(LocalDate.now())
            );
        }
        return filteredEvents;
    }




    private boolean checkEventsForRequiredFields(KudagoEventDto event) {
        if (event.getCategories().isEmpty()) {
            log.error("Categories in kudaGoEvent with id: {} is empty", event.getId());
            return false;
        }
        if (event.getTitle() == null) {
            log.error("Name in kudaGoEvent with id: {} is null", event.getId());
            return false;
        }
        if (event.getLocation() == null) {
            log.error("Event with id: {} has no location", event.getId());
            return false;
        }
        if (event.getDates() == null  || event.getDates().isEmpty()) {
            log.error("Event with id: {} has no dates", event.getId());
            return false;
        }
        if (event.getDescription() == null) {
            log.error("Event with id: {} has no description", event.getId());
            return false;
        }
        if (event.getAgeRestriction() == null) {
            log.error("Event with id: {} has no age restriction", event.getId());
            return false;
        }
        return true;
    }

    private List<KudagoEventDto> filterEvents(Map<Long, KudagoEventDto> events, int daysQuantityToFirstPublication) {
        return events.values().stream()
                .filter(kudagoEvent -> {
                    return kudagoEvent
                            .getPublicationDate()
                            .isAfter(LocalDate.now().minusDays(daysQuantityToFirstPublication));
                })
                .filter(this::checkEventsForRequiredFields)

                .toList();
    }


}
