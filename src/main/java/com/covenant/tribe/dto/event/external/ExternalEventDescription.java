package com.covenant.tribe.dto.event.external;

public record ExternalEventDescription(
        Long dbEventId, Long externalEventId, Double similarityPercentage
) {
}
