package com.covenant.tribe.repository;

import com.covenant.tribe.domain.event.EventType;

import java.util.Optional;

public interface CustomEventTypeRepository {
    Optional<EventType> findEventTypeByIdFetchEventListWithTypeAndTagList(Long eventTypeId);
}
