package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.dto.event.EventTypeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public interface EventTypeService {

    EventType save(EventType eventType);

    EventType findEventTypeById(Long eventTypeId);

    List<EventTypeDTO> getAllRectangleEventTypes(boolean isDark);

    List<EventTypeDTO> getAllCircleEventTypes(boolean isDark);

    EventType getEventTypeByName(String eventTypeName);

    EventType getEventTypeByIdFetchEventListWithTypeAndTagList(Long eventTypeId);
}
