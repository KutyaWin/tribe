package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.search.EventSearchUnit;
import com.covenant.tribe.domain.event.search.EventSearchUnitFactory;
import com.covenant.tribe.repository.EventSearchUnitRepository;
import com.covenant.tribe.service.EventSearchService;
import com.covenant.tribe.service.EventService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventSearchServiceImpl implements EventSearchService {

    EventSearchUnitFactory eventSearchUnitFactory;

    EventSearchUnitRepository eventSearchUnitRepository;

    EventService eventService;

    @Override
    @Transactional
    public EventSearchUnit create(Event event) {
        EventSearchUnit eventSearchUnit = eventSearchUnitFactory.create(event);
        return eventSearchUnitRepository.save(eventSearchUnit);
    }

    @Override
    @Transactional
    public void updateAll() {
        Integer size = 1000;
        int i = 0;
        eventSearchUnitRepository.deleteAll();
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
            create(event);
        }
    }
}
