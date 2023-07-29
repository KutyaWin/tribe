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

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
            OffsetDateTime eventStart = null;
            OffsetDateTime eventEnd = null;
            if (zoneOffset != null) {
                eventStart = OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(dates.get(0).getStart()),
                        zoneOffset
                );
                eventEnd = OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(dates.get(dates.size() - 1).getEnd()),
                        zoneOffset
                );
            } else {
                eventStart = OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(dates.get(0).getStart()),
                        MOSCOW_TIMEZONE_OFFSET
                );
                eventEnd = OffsetDateTime.ofInstant(
                        Instant.ofEpochMilli(dates.get(dates.size() - 1).getEnd()),
                        MOSCOW_TIMEZONE_OFFSET
                );
            }
            externalEventDates.put(
                    kudagoEvent.getId(), new ExternalEventDates(eventStart, eventEnd)
            );
        }
        return externalEventDates;
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
