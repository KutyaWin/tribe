package com.covenant.tribe.service.facade;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.dto.event.SearchEventDTO;
import com.covenant.tribe.util.querydsl.EventFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EventSearchFacade {
    Page<SearchEventDTO> getEventsByFilter(EventFilter eventFilter, Long currentUserId, Integer page, Integer size) throws JsonProcessingException;

    void updateAll();

    void updateAll(List<Event> events);
}
