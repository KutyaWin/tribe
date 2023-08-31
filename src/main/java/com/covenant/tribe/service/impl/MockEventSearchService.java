package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventIdView;
import com.covenant.tribe.domain.event.search.EventSearchUnit;
import com.covenant.tribe.service.EventSearchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(value = "elastic.enabled", havingValue = "false")
public class MockEventSearchService implements EventSearchService {
    @Override
    public EventSearchUnit create(Event event) {
        return null;
    }

    @Override
    public EventSearchUnit updateOrSave(Event event)  {
        return null;
    }

    @Override
    public List<EventSearchUnit> findByText(String text, Pageable pageable) {
        return null;
    }

    @Override
    public List<EventSearchUnit> findByText(String text, Pageable pageable, List<EventIdView> ids) {
        return null;
    }

    @Override
    public void delete(Event event) {

    }

    @Override
    public void deleteAll() {

    }
}
