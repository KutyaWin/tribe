package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.dto.event.EventTagDTO;
import com.covenant.tribe.util.mapper.EventTagMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class EventTagMapperImpl implements EventTagMapper {

    public EventTagDTO mapEventTagToEventTagDTO(Tag eventTag) {
        log.debug("map EventTag to EventTagDTO. Passed Tag: {}", eventTag);
        return EventTagDTO.builder()
                .id(eventTag.getId())
                .name(eventTag.getTagName())
                .build();
    }

    @Override
    public List<EventTagDTO> mapEventTagListToEventTagDtoList(List<Tag> eventTags) {
        return eventTags.stream()
                .map(this::mapEventTagToEventTagDTO)
                .toList();
    }

}
