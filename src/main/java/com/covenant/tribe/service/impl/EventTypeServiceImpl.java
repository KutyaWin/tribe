package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.exeption.event.EventTypeNotFoundException;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.service.EventTypeService;
import com.covenant.tribe.util.mapper.EventMapper;
import com.covenant.tribe.util.mapper.EventTypeMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class EventTypeServiceImpl implements EventTypeService {

    EventTypeRepository eventTypeRepository;
    EventTypeMapper eventTypeMapper;

    public EventType getEventTypeByName(String eventTypeName) {
        return eventTypeRepository.findEventTypeByTypeName(eventTypeName)
                .orElseThrow(() -> {
                    log.error("[EXCEPTION]: EventType with name: {}, not found", eventTypeName);
                    return new EventTypeNotFoundException(
                            String.format("[EXCEPTION]: EventType with name: %s, not found", eventTypeName)
                    );
                });
    }

    @Override
    public List<EventTypeDTO> getAllEventTypes() {
        List<EventType> eventTypes = eventTypeRepository.findAll();
        return eventTypeMapper.mapToEventTypeDTOList(eventTypes);
    }
}
