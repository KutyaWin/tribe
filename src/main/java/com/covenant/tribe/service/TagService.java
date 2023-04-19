package com.covenant.tribe.service;

import com.covenant.tribe.domain.Tag;
import org.springframework.stereotype.Service;

@Service
public interface TagService {
    Tag findTagOrSaveByTagName(String tagName);
}
