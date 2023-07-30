package com.covenant.tribe.dto.event.external;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record ExternalEventDates(
        OffsetDateTime start,
        OffsetDateTime end,
        LocalDate publicationDate
) {
}
