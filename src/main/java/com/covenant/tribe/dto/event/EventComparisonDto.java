package com.covenant.tribe.dto.event;

import lombok.Builder;

public record EventComparisonDto(
        Long eventId, String eventDescription
) {
}
