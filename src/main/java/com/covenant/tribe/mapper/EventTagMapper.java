package com.covenant.tribe.mapper;

import com.covenant.tribe.domain.event.EventTag;
import com.covenant.tribe.dto.event.EventTagDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventTagMapper {

    public EventTagDTO mapEventTagToEventTagDTO(EventTag eventTag) {
        log.debug("map EventTag to EventTagDTO. Passed Tag: {}", eventTag);
        return EventTagDTO.builder()
                .id(eventTag.getId())
                .name(eventTag.getName())
                .build();
    }

}
