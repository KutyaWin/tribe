package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoDate;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.EventCategory;
import com.covenant.tribe.repository.KudaGoEventRepository;
import com.covenant.tribe.service.ExternalEventService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalEventServiceImpl implements ExternalEventService {

    final Set<String> EXTRA_KUDA_GO_CATEGORIES = Set.of(EventCategory.STOCKS.getKudaGoName());
    final Map<String, String> CATEGORY_NAMES_FOR_MATCHING = getCategoryNamesForMatching();

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
        List<KudagoEventDto> eventsAfterDeletingStartDates = deleteExpiredStartDates(filteredEvents);
        return changeKudaGoCategoriesToTribeCategories(eventsAfterDeletingStartDates);
    }

    @Transactional
    @Override
    public void saveNewExternalEvents(
            List<KudagoEventDto> kudaGoEvents,
            Map<Long, ReverseGeocodingData> reverseGeocodingData,
            Map<Long, List<String>> imageFileNames,
            Map<Long, List<Long>> eventTagIds
    ) {

    }



    private List<KudagoEventDto> deleteExpiredStartDates(List<KudagoEventDto> filteredEvents) {
        for(KudagoEventDto event : filteredEvents) {
            event.getDates().removeIf(
                    date -> LocalDate.parse(date.getStartDate()).isBefore(LocalDate.now())
            );
        }
        return filteredEvents;
    }

    private List<KudagoEventDto> changeKudaGoCategoriesToTribeCategories(List<KudagoEventDto> events) {
        for (KudagoEventDto event : events) {
            List<String> categories = event.getCategories();
            String tribeCategory = CATEGORY_NAMES_FOR_MATCHING.get(categories.get(0));
            event.setCategories(List.of(tribeCategory));
        }
        return events;
    }

    private Map<String, String> getCategoryNamesForMatching() {
        Map<String, String> categoryNamesForMatching = new HashMap<>();
        for(EventCategory eventCategory : EventCategory.values()) {
            categoryNamesForMatching.put(eventCategory.getKudaGoName(), eventCategory.getTribeName());
        }
        return categoryNamesForMatching;
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

    private boolean checkForExtraCategories(List<String> kudaGoCategories) {
        for(String kudaGoCategoryName : kudaGoCategories) {
            if (EXTRA_KUDA_GO_CATEGORIES.contains(kudaGoCategoryName)) {
                return true;
            }
        }
        return false;
    }

    private List<KudagoEventDto> filterEvents(Map<Long, KudagoEventDto> events, int daysQuantityToFirstPublication) {
        return events.values().stream()
                .filter(kudagoEvent -> {
                    return kudagoEvent
                            .getPublicationDate()
                            .isAfter(LocalDate.now().minusDays(daysQuantityToFirstPublication));
                })
                .filter(this::checkEventsForRequiredFields)
                .filter(kudagoEventDto -> !checkForExtraCategories(kudagoEventDto.getCategories()))
                .toList();
    }


}
