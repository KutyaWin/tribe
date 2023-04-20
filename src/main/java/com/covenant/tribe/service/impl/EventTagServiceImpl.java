package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.EventTag;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.exeption.event.EventTypeNotFoundException;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.service.EventTagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class EventTagServiceImpl implements EventTagService {

    EventTypeRepository eventTypeRepository;
    @Override
    public Set<EventTag> getAllTagsByEventTypeId(Long eventTypeId) {
        EventType eventType = eventTypeRepository
                .findById(eventTypeId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "Event type with %s  does not exist",
                            eventTypeId
                    );
                    throw new EventTypeNotFoundException(message);
                });
        return eventType.getTagList();
    }
}
