package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.util.mapper.EventTypeMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class EventTypeMapperIml implements EventTypeMapper {

    @Override
    public List<EventTypeDTO> mapToEventTypeDTOList(List<EventType> eventTypeList) {
        log.debug("map EventTypeList to EventTypeDTOList. Passed eventTypeList: {}", eventTypeList);

        return eventTypeList.stream()
                .map(eventType -> EventTypeDTO.builder()
                        .id(eventType.getId())
                        .typeName(eventType.getTypeName())
                        .animationJson(eventType.getAnimationJson())
                        .build())
                .collect(Collectors.toList());
    }
}
