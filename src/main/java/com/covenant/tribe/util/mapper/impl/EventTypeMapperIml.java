package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.dto.event.EventTypeInfoDto;
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
    public List<EventTypeDTO> mapToLightRectangleEventTypeDTOList(List<EventType> eventTypeList) {

        return eventTypeList.stream()
                .map(eventType -> EventTypeDTO.builder()
                        .id(eventType.getId())
                        .typeName(eventType.getTypeName())
                        .animationJson(eventType.getLightRectangleAnimation())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<EventTypeDTO> mapToLightCircleEventTypeDTOList(List<EventType> eventTypeList) {

        return eventTypeList.stream()
                .map(eventType -> EventTypeDTO.builder()
                        .id(eventType.getId())
                        .typeName(eventType.getTypeName())
                        .animationJson(eventType.getLightCircleAnimation())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<EventTypeDTO> mapToDarkRectangleEventTypeDTOList(List<EventType> eventTypeList) {

        return eventTypeList.stream()
                .map(eventType -> EventTypeDTO.builder()
                        .id(eventType.getId())
                        .typeName(eventType.getTypeName())
                        .animationJson(eventType.getDarkRectangleAnimation())
                        .build())
                .collect(Collectors.toList());
    }

    public EventTypeDTO mapToEventTypeDto(EventType eventType, String animationJson) {
        return EventTypeDTO.builder()
                .id(eventType.getId())
                .typeName(eventType.getTypeName())
                .animationJson(animationJson)
                .build();
    }

    @Override
    public List<EventTypeDTO> maptoDarkCircleEventTypeDTOList(List<EventType> eventTypeList) {

        return eventTypeList.stream()
                .map(eventType -> EventTypeDTO.builder()
                        .id(eventType.getId())
                        .typeName(eventType.getTypeName())
                        .animationJson(eventType.getDarkCircleAnimation())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public EventTypeInfoDto mapToEventTypeInfoDto(EventType eventType) {
        return EventTypeInfoDto.builder()
                .id(eventType.getId())
                .typeName(eventType.getTypeName())
                .build();
    }
}
