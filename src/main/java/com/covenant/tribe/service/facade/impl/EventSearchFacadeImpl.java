package com.covenant.tribe.service.facade.impl;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventIdView;
import com.covenant.tribe.domain.event.search.EventSearchUnit;
import com.covenant.tribe.dto.event.SearchEventDTO;
import com.covenant.tribe.service.EventSearchService;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.facade.EventSearchFacade;
import com.covenant.tribe.util.mapper.EventMapper;
import com.covenant.tribe.util.querydsl.EventFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.querydsl.core.types.Predicate;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventSearchFacadeImpl implements EventSearchFacade {
    EventService eventService;

    EventSearchService eventSearchService;

    EventMapper eventMapper;
    @Override
    @Transactional
    public Page<SearchEventDTO> getEventsByFilter(EventFilter filter, Long currentUserId, Integer page, Integer size) throws JsonProcessingException {
        Pair<Predicate, Pageable> predicateAndPageable = eventService.getPredicateForFilters(filter, currentUserId, page, size);
        Predicate predicate = predicateAndPageable.getLeft();
        Pageable pageable = predicateAndPageable.getRight();
        Page<Event> filteredEvents = getAll(filter, pageable, predicate);
        if (currentUserId != null) {
            List<UserRelationsWithEvent> eventsCurrentUser = eventService.getUserRelationsWithEvents(currentUserId);
            return filteredEvents.map(event -> eventMapper.mapToSearchEventDTO(event, eventsCurrentUser));
        }
        return filteredEvents.map(eventMapper::mapToSearchEventDTO);
    }

    private Page<Event> getAll(EventFilter filter, Pageable pageable, Predicate predicate) throws JsonProcessingException {
        if (filter.getText() == null) return eventService.findAll(pageable, predicate);
        List<EventIdView> ids = eventService.findIdsByPredicate(predicate);
        List<EventSearchUnit> byTextAndIds = eventSearchService.findByTextAndIds(filter.getText(), pageable, ids);
        List<Long> collect = byTextAndIds.stream().map(EventSearchUnit::getId).collect(Collectors.toList());
        List<Event> byIdIn = eventService.getByIdIn(collect);
        Event[] list = new Event[byIdIn.size()];
        for (Event event : byIdIn) {
            list[collect.indexOf(event.getId())] = event;
        }
        PageImpl<Event> events = new PageImpl<>(Arrays.stream(list).toList());
        return events;
    }

    @Override
    @Transactional
    public void updateAll() {
        Integer size = 1000;
        int i = 0;
        eventSearchService.deleteAll();
        while (true) {
            List<Event> all = eventService.findAll(i, size);
            if (all.size() == 0) break;
            updateAll(all);
            i++;
        }
    }

    @Override
    @Transactional
    public void updateAll(List<Event> events) {
        for (Event event : events) {
            eventSearchService.create(event);
        }
    }
}
