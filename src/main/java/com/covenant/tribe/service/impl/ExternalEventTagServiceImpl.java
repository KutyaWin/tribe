package com.covenant.tribe.service.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.exeption.event.EventTypeNotFoundException;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.repository.TagRepository;
import com.covenant.tribe.service.ExternalEventTagService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ExternalEventTagServiceImpl implements ExternalEventTagService {


    TagRepository tagRepository;
    EventTypeRepository eventTypeRepository;

    @Transactional
    @Override
    public Map<Long, List<Long>> handleNewExternalTags(List<KudagoEventDto> kudaGoEvents) {
        Map<Long, List<Long>> eventsWithTags = new HashMap<>();
        for (KudagoEventDto kudagoEvent : kudaGoEvents) {
            List<String> allTags = kudagoEvent.getTags();
            Set<String> tagsInLowerCase = allTags.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            Set<String> existTagNames = tagRepository.findAllByTagNames(tagsInLowerCase);
            Set<Tag> existTags = tagRepository.findAllByTagNameIn(existTagNames);
            allTags.removeAll(existTagNames);
            Set<Tag> newTagEntities = allTags.stream()
                    .map(tagName -> {
                        return Tag.builder()
                                .tagName(tagName)
                                .tagNameEn("")
                                .build();
                    })
                    .collect(Collectors.toSet());
            tagRepository.saveAll(newTagEntities);
            existTags.addAll(newTagEntities);
            EventType eventType = eventTypeRepository
                    .findEventTypeByTypeName(kudagoEvent.getCategories().get(0))
                    .orElseThrow(() -> {
                        String erMessage = "There is no event type with name %s"
                                .formatted(kudagoEvent.getCategories().get(0));
                        log.error(erMessage);
                        return new EventTypeNotFoundException(erMessage);
                    });
            eventType.addTags(existTags);
            eventTypeRepository.save(eventType);

            List<Long> tagIds = existTags.stream()
                    .map(Tag::getId)
                    .toList();
            eventsWithTags.put(kudagoEvent.getId(), tagIds);
        }
        return eventsWithTags;
    }
}
