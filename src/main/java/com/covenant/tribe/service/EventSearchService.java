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

    EventSearchUnit updateOrSave(Event event);

    List<EventSearchUnit> findByText(String text, Pageable pageable) throws JsonProcessingException;

    List<EventSearchUnit> findByText(String text, Pageable pageable, List<EventIdView> ids) throws JsonProcessingException;

    void delete(Event event);

    void deleteAll();
}