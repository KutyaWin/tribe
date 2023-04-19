package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.EventAddressDTO;
import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;

import java.util.List;

public interface EventMapper {

    Event mapToEvent(RequestTemplateForCreatingEventDTO requestTemplateForCreatingEventDTO);
    DetailedEventInSearchDTO mapToDetailedEventInSearchDTO(Event event, Long userId);
}
