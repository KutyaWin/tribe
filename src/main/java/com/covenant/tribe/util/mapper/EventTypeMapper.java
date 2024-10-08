package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.dto.event.EventTypeInfoDto;

import java.util.List;

public interface EventTypeMapper {

    List<EventTypeDTO> mapToLightRectangleEventTypeDTOList(List<EventType> eventTypeList);
    List<EventTypeDTO> mapToLightCircleEventTypeDTOList(List<EventType> eventTypeList);
    List<EventTypeDTO> mapToDarkRectangleEventTypeDTOList(List<EventType> eventTypeList);
    List<EventTypeDTO> maptoDarkCircleEventTypeDTOList(List<EventType> eventTypeList);

    EventTypeDTO mapToEventTypeDto(EventType eventType, String animationJson);

    EventTypeInfoDto mapToEventTypeInfoDto(EventType eventType);
}
