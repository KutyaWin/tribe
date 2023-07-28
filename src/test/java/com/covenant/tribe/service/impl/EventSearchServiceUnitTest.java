package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.search.EventAddressSearchUnit;
import com.covenant.tribe.domain.event.search.EventSearchUnit;
import com.covenant.tribe.domain.event.search.EventSearchUnitFactory;
import com.covenant.tribe.repository.EventSearchUnitRepository;
import com.covenant.tribe.service.EventSearchService;
import com.covenant.tribe.service.EventService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EventSearchServiceUnitTest {

    @Mock
    EventSearchUnitFactory eventSearchUnitFactory;

    @Mock
    EventSearchUnitRepository eventSearchUnitRepository;

    @Mock
    ElasticsearchTemplate client;

    @InjectMocks
    EventSearchServiceImpl eventService;
    @Test
    public void events_properly_updated() throws IllegalAccessException {
        Event firstEvent = getEvent();
        EventSearchUnit firstUnit = getSearchUnit(firstEvent);
        Event secondEvent = getSecondEvent();
        EventSearchUnit secondSearchUnit = getSearchUnit(secondEvent);
        when(eventSearchUnitRepository.findById(String.valueOf(1000L))).thenReturn(Optional.of(firstUnit));
        when(eventSearchUnitFactory.create(secondEvent)).thenReturn(secondSearchUnit);
        when(eventSearchUnitRepository.save(any())).thenReturn(firstUnit);
        EventSearchUnit updated = eventService.update(secondEvent);
        assertEquals(updated, secondSearchUnit);
    }

    private  EventSearchUnit getSearchUnit(Event event) {
        EventAddress eventAddress = event.getEventAddress();
        EventAddressSearchUnit build = EventAddressSearchUnit.builder()
                .eventLatitude(eventAddress.getEventLatitude())
                .eventLongitude(eventAddress.getEventLongitude())
                .names(getAddressNames(eventAddress))
                .build();
        return EventSearchUnit.builder()
                .id(event.getId())
                .eventAddress(build)
                .eventName(event.getEventName())
                .eventDescription(event.getEventDescription())
                .eventType(null)
                .taglist(event.getTagList().stream().map(Tag::getTagName).toList())
                .users(new ArrayList<>())
                .build();
    }

    private static Event getEvent() {
        EventAddress eventAddress = getEventAddress();
        Event build = Event.builder()
                .id(1000L)
                .eventDescription("des")
                .eventType(null)
                .eventName("name")
                .eventAddress(eventAddress)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now())
                .isPrivate(false)
                .showEventInSearch(true)
                .createdAt(OffsetDateTime.now())
                .build();
        return build;
    }

    private static Event getSecondEvent() {
        EventAddress eventAddress = getEventAddress();
        Event build = Event.builder()
                .id(1000L)
                .eventDescription("dssss")
                .eventType(null)
                .eventName("name2")
                .eventAddress(eventAddress)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now())
                .isPrivate(true)
                .showEventInSearch(false)
                .createdAt(OffsetDateTime.now())
                .build();
        return build;
    }

    private ArrayList<String> getAddressNames(EventAddress eventAddress) {
        ArrayList<String> addressNames = new ArrayList<>();
        addressNames.add(eventAddress.getCity());
        addressNames.add(eventAddress.getStreet());
        addressNames.add(eventAddress.getDistrict());
        addressNames.add(eventAddress.getBuilding());
        addressNames.add(eventAddress.getHouseNumber());
        return addressNames;
    }

    @NotNull
    private static EventAddress getEventAddress() {
        return new EventAddress(1000L, 0.00001D, 0.00001D, "SPB", "SPB", "Nevsky",
                "Center", "24", "2", "2");
    }

    ;
}
