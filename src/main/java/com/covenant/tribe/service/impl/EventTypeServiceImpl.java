package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.service.EventTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class EventTypeServiceImpl implements EventTypeService {

    EventTypeRepository eventTypeRepository;

    @Override
    public List<EventTypeDTO> getAllEventTypes() {
        List<EventType> eventTypes = eventTypeRepository.findAll();
        return mapEventTypeListToEventTypesDTOList(eventTypes);
    }

    private List<EventTypeDTO> mapEventTypeListToEventTypesDTOList(List<EventType> eventTypes) {
        return eventTypes.stream()
                .map(eventType -> new EventTypeDTO(eventType.getId(), eventType.getName()))
                .collect(Collectors.toList());
    }
}
