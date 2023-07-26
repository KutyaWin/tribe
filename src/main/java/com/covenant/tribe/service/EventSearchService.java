package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventIdView;
import com.covenant.tribe.domain.event.search.EventSearchUnit;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventSearchService {
    EventSearchUnit create(Event event);

    List<EventSearchUnit> findByTextAndIds(String text, Pageable pageable) throws JsonProcessingException;

    List<EventSearchUnit> findByTextAndIds(String text, Pageable pageable, List<EventIdView> ids) throws JsonProcessingException;

    void deleteAll();
}