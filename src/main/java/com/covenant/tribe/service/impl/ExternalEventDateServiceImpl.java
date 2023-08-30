package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoDate;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import com.covenant.tribe.service.ExternalEventDateService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExternalEventDateServiceImpl implements ExternalEventDateService {

    final ZoneOffset MOSCOW_TIMEZONE_OFFSET = ZoneOffset.ofHours(3);

    @Override
    public Map<Long, ExternalEventDates> handleExternalEventDates(
            List<KudagoEventDto> kudaGoEvents
    ) {
        Map<Long, ExternalEventDates> externalEventDates = new HashMap<>();
        for (KudagoEventDto kudagoEvent : kudaGoEvents) {
            ZoneOffset zoneOffset = null;
            List<KudagoDate> dates = kudagoEvent.getDates();
            if (kudagoEvent.getLocation().getTimezone() != null) {
                zoneOffset = getZoneOffset(kudagoEvent.getLocation().getTimezone());
            }
            LocalDateTime eventStart = null;
            LocalDateTime eventEnd = null;
            LocalDate publicationDate = null;
            if (zoneOffset != null) {
                eventStart = LocalDateTime.ofEpochSecond(dates.get(0).getStart(), 0, zoneOffset);
                eventEnd = LocalDateTime.ofEpochSecond(dates.get(dates.size() - 1).getEnd(), 0, zoneOffset);
                publicationDate = LocalDateTime
                        .ofEpochSecond(kudagoEvent.getPublicationDate(), 0, zoneOffset).toLocalDate();
            } else {
                eventStart = LocalDateTime.ofEpochSecond(
                        dates.get(0).getStart(), 0, MOSCOW_TIMEZONE_OFFSET);
                eventEnd = LocalDateTime.ofEpochSecond(
                        dates.get(dates.size() - 1).getEnd(), 0, MOSCOW_TIMEZONE_OFFSET
                );
                publicationDate = LocalDateTime
                        .ofEpochSecond(kudagoEvent.getPublicationDate(), 0, MOSCOW_TIMEZONE_OFFSET)
                        .toLocalDate();
            }
            externalEventDates.put(
                    kudagoEvent.getId(), new ExternalEventDates(eventStart, eventEnd, publicationDate)
            );
        }
        return externalEventDates;
    }

    @Override
    public LocalDateTime transformTimestampToLocalDateTime(KudagoDate kudagoDate, String timezone) {
        Long timestampStart = kudagoDate.getStart();
        if (timezone == null) {
            return LocalDateTime.ofEpochSecond(
                    timestampStart, 0, MOSCOW_TIMEZONE_OFFSET);
        }
        ZoneOffset zoneOffset = getZoneOffset(timezone);
        return LocalDateTime.ofEpochSecond(timestampStart, 0, zoneOffset);
    }

    private ZoneOffset getZoneOffset(String timezone) {
        if (timezone.contains("/")) {
            ZoneId zoneId = ZoneId.of(timezone);
            return zoneId.getRules().getOffset(Instant.now());
        } else {
            String timeZoneWithoutGmt = timezone.replace("GMT", "");
            return ZoneOffset.of(timeZoneWithoutGmt);
        }
    }
}
