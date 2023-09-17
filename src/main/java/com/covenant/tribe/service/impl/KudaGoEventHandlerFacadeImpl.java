package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoClientParams;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.event.EventContactInfo;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.external.ExternalEventAddressDto;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import com.covenant.tribe.service.*;
import com.covenant.tribe.service.facade.ExternalEventAddressHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KudaGoEventHandlerFacadeImpl implements ExternalEventHandlerFacade {


    KudagoFetchService kudagoFetchService;
    ExternalEventService externalEventService;
    ReverseGeolocationService reverseGeolocationService;
    ExternalEventAddressHandler externalEventAddressHandler;
    ExternalImageStorageService externalImageStorageService;
    ExternalEventTagService externalEventTagService;
    ExternalEventDateService externalEventDateService;
    ExternalEventContactService externalEventContactService;


    @Transactional
    @Override
    public void handleNewEvents(String sincePublicationDate) {
        Optional<Map<Long, KudagoEventDto>> kudaGoEventsOpt = Optional.empty();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime minPublicationDate = LocalDate
                .parse(sincePublicationDate, formatter)
                .atStartOfDay();
        Long timestampLong = minPublicationDate.toEpochSecond(ZoneOffset.ofHours(3));
        try {
            kudaGoEventsOpt = Optional.of(kudagoFetchService
                    .fetchPosts(timestampLong));
        } catch (JsonProcessingException e) {
            String erMessage = "[EXCEPTION]: Failed to process data from KudaGo. Error: %s".
                    formatted(e.getMessage());
            log.error(erMessage);
        }

        List<KudagoEventDto> eventsAfterDeletingExiting = null;
        if (kudaGoEventsOpt.isPresent()) {
            Map<Long, KudagoEventDto> kudaGoEvents = kudaGoEventsOpt.get();
            eventsAfterDeletingExiting = externalEventService.prepareEventsForCreating(
                    kudaGoEvents, minPublicationDate.minusDays(1)
            );
        } else {
            eventsAfterDeletingExiting = new ArrayList<>();
        }

        Map<Long, EventAddressDTO> addresses = new HashMap<>();
        eventsAfterDeletingExiting.forEach(event -> {
            EventAddressDTO externalEventAddressDto =
                    externalEventAddressHandler.handleExternalEventAddress(event);
            addresses.put(event.getId(), externalEventAddressDto);
        });
        List<KudagoEventDto> eventsAfterDeletingNullAddresses = eventsAfterDeletingExiting.stream()
                .filter(event -> addresses.get(event.getId()) != null)
                .toList();

        Map<Long, List<String>> images = externalImageStorageService.saveExternalImages(
                eventsAfterDeletingNullAddresses
        );

        Map<Long, List<Long>> eventTagIds = externalEventTagService
                .handleNewExternalTags(eventsAfterDeletingNullAddresses);

        Map<Long, List<EventContactInfo>> eventContactInfos = externalEventContactService
                .handleEventContactsInfo(eventsAfterDeletingNullAddresses);


        Map<Long, ExternalEventDates> externalEventDates = externalEventDateService
                .handleExternalEventDates(eventsAfterDeletingNullAddresses);

        externalEventService.saveNewExternalEvents(
                eventsAfterDeletingNullAddresses,
                eventContactInfos,
                addresses,
                images,
                eventTagIds,
                externalEventDates
        );

    }
}
