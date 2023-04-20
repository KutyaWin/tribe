package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.dto.event.EventTypeDTO;

import java.util.List;

public interface EventTypeMapper {

    List<EventTypeDTO> mapToEventTypeDTOList(List<EventType> eventTypeList);
}
