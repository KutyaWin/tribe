package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoClientParams;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import com.covenant.tribe.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class KudaGoEventHandlerFacadeImpl implements ExternalEventHandlerFacade {


    KudagoFetchService kudagoFetchService;
    ExternalEventService externalEventService;
    ReverseGeolocationService reverseGeolocationService;
    ExternalImageStorageService externalImageStorageService;
    ExternalEventTagService externalEventTagService;
    ExternalEventDateService externalEventDateService;


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
        }
        Map<Long, ReverseGeocodingData> reverseGeocodingData = reverseGeolocationService.getExternalEventAddresses(
                eventsAfterDeletingExiting
        );

        Map<Long, List<String>> images = externalImageStorageService.saveExternalImages(
                eventsAfterDeletingExiting
        );

        Map<Long, List<Long>> eventTagIds = externalEventTagService
                .handleNewExternalTags(eventsAfterDeletingExiting);


        Map<Long, ExternalEventDates> externalEventDates = externalEventDateService
                .handleExternalEventDates(eventsAfterDeletingExiting);

        externalEventService.saveNewExternalEvents(
                eventsAfterDeletingExiting,
                reverseGeocodingData,
                images,
                eventTagIds,
                externalEventDates
        );

    }
}
