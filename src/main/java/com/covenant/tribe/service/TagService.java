package com.covenant.tribe.service;

import com.covenant.tribe.domain.Tag;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface TagService {

    Tag findByName(String tagName);

    List<Tag> saveAll(Set<String> tags);

    List<Tag> findTagsByTagId(Set<Long> tagsId);

    Tag saveTag(Tag tag);

    Tag getTagOrSaveByTagName(String tagName);

    List<Tag> findTagsByTagNameContaining(String tagName);

    List<String> getTagsByContainingName(String tagName);

    List<Tag> getAllTagsByEventTypeId(Long eventTypeId);

    boolean isExistTagByName(String tagName);

    List<Tag> findAllByIdFetchEventListWithTagAndEventTypesToWhichTagBelong(Set<Long> ids);
}
