package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.repository.TagRepository;
import com.covenant.tribe.service.TagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class TagServiceImpl implements TagService {

    TagRepository tagRepository;

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
}
