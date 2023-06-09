package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.exeption.event.EventTypeNotFoundException;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.repository.TagRepository;
import com.covenant.tribe.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class TagServiceImpl implements TagService {

    TagRepository tagRepository;
    EventTypeRepository eventTypeRepository;

    @Transactional
    @Override
    public List<Tag> saveAll(Set<String> tags) {
        Set<Tag> newTags = tags.stream()
                .map(String::toLowerCase)
                .filter(t -> !this.isExistTagByName(t))
                .map(tagName -> Tag.builder().tagName(tagName).build())
                .collect(Collectors.toSet());

        return tagRepository.saveAll(newTags);
    }

    public boolean isExistTagByName(String tagName) {
        return tagRepository.existsByTagName(tagName).isPresent();
    }

    @Override
    public Tag findByName(String tagName) {
        return tagRepository.findByTagName(tagName);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Tag> findTagsByTagId(Set<Long> tagsId) {
        return tagRepository.findAllById(tagsId);
    }

    @Override
    public Tag saveTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> findTagsByTagNameContaining(String tagName) {
        return tagRepository.findAllByTagNameContainingIgnoreCase(tagName);
    }

    @Transactional
    @Override
    public Tag getTagOrSaveByTagName(String tagName) {
        return tagRepository.findTagByTagName(tagName)
                .orElseGet(() -> saveTag(Tag.builder().tagName(tagName).build()));
    }

    @Override
    public List<String> getTagsByContainingName(String tagName) {
        return findTagsByTagNameContaining(tagName).stream()
                .map(Tag::getTagName)
                .toList();
    }

    @Override
    public List<Tag> getAllTagsByEventTypeId(Long eventTypeId) {
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

    @Override
    public List<Tag> findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong(Set<Long> ids) {
        return tagRepository.findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong(ids);
    }
}
