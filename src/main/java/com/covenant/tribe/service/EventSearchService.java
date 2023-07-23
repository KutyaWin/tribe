package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.search.EventSearchUnit;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventSearchService {
    EventSearchUnit create(Event event);

    @Transactional
    void updateAll();

    @Transactional
    void updateAll(List<Event> events);
}