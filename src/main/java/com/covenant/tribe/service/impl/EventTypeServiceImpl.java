package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.dto.event.EventTypeDTO;
import com.covenant.tribe.dto.event.EventTypeInfoDto;
import com.covenant.tribe.exeption.event.EventTypeNotFoundException;
import com.covenant.tribe.exeption.storage.FilesNotHandleException;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.repository.FileStorageRepository;
import com.covenant.tribe.service.EventTypeService;
import com.covenant.tribe.util.mapper.EventTypeMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class EventTypeServiceImpl implements EventTypeService {

    EventTypeRepository eventTypeRepository;
    FileStorageRepository fileStorageRepository;
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

    @Transactional(readOnly = true)
    @Override
    public List<EventTypeInfoDto> getEventTypeInfo() {
        return eventTypeRepository.findAll()
                .stream()
                .map(eventTypeMapper::mapToEventTypeInfoDtoList)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventTypeDTO> getAllRectangleEventTypes(boolean isDark) {
        List<EventType> eventTypes = eventTypeRepository.findAll();
        return eventTypes.stream()
                .map(eventType -> {
                    String fileName = isDark ?
                            eventType.getDarkRectangleAnimation() : eventType.getLightRectangleAnimation();
                    try {
                        String animation = fileStorageRepository.getRectangleAnimationJson(fileName);
                        return eventTypeMapper.mapToEventTypeDto(eventType, animation);
                    } catch (IOException e) {
                        String message = String.format("[EXCEPTION]: File %s not found", fileName);
                        log.error(message);
                        throw new FilesNotHandleException(message);
                    }

                })
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventTypeDTO> getAllCircleEventTypes(boolean isDark) {
        List<EventType> eventTypes = eventTypeRepository.findAll();
        return eventTypes.stream()
                .map(eventType -> {
                    String fileName = isDark ?
                            eventType.getDarkCircleAnimation() : eventType.getLightCircleAnimation();
                    try {
                        String animation = fileStorageRepository.getCircleAnimationJson(fileName);
                        return eventTypeMapper.mapToEventTypeDto(eventType, animation);
                    } catch (IOException e) {
                        String message = String.format("[EXCEPTION]: File %s not found", fileName);
                        log.error(message);
                        throw new FilesNotHandleException(message);
                    }

                })
                .toList();
    }
}
