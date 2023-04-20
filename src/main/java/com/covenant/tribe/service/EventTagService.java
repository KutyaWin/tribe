package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.EventTag;
import com.covenant.tribe.dto.event.EventTagDTO;

import java.util.Set;


public interface EventTagService {
    Set<EventTag> getAllTagsByEventTypeId(Long eventTypeId);
}
