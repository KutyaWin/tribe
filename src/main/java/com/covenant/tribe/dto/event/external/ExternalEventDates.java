package com.covenant.tribe.dto.event.external;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExternalEventDates(
        LocalDateTime start,
        LocalDateTime end,
        LocalDate publicationDate
) {
}
