package com.covenant.tribe.service;

import com.covenant.tribe.domain.Tag;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TagService {

    Tag saveTag(Tag tag);

    Tag getTagOrSaveByTagName(String tagName);

    List<Tag> findTagsByTagNameContaining(String tagName);

    List<String> getTagsByContainingName(String tagName);
}
