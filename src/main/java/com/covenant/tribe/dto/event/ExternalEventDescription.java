package com.covenant.tribe.dto.event;

public record ExternalEventDescription(
        Long dbEventId, Long externalEventId, Double similarityPercentage
) {
}
