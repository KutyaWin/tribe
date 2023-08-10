package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.service.EventService;
import com.covenant.tribe.service.EventTypeService;
import com.covenant.tribe.service.UserService;
import com.covenant.tribe.service.facade.impl.EventFacadeImpl;
import com.covenant.tribe.util.mapper.EventMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

//Unit test
@ExtendWith(MockitoExtension.class)
class EventFacadeImplUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private EventTypeService eventTypeService;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private EventService eventService;
    @Mock
    private FirebaseServiceImpl firebaseService;

    @InjectMocks
    private EventFacadeImpl eventFacade;

    private static final Faker FAKER = new Faker();

    @Test
    void handleNewEvent_shouldHandleNewEventWithPassedNullDataForUnnecessaryFieldsRequestTemplate() {
        //given
        RequestTemplateForCreatingEventDTO request = RequestTemplateForCreatingEventDTO.builder()
                .eventTypeId(FAKER.number().randomNumber())
                .eventName(FAKER.funnyName().name())
                .eventAddress(null)
                .startTime(OffsetDateTime.now())
                .endTime(OffsetDateTime.now().plus(5, ChronoUnit.HOURS))
                .newEventTagNames(null)
                .eventTagIds(null)
                .description(null)
                .avatarsForAdding(null)
                .avatarsForAdding(null)
                .invitedUserIds(null)
                .showEventInSearch(true)
                .isPrivate(false)
                .sendToAllUsersByInterests(true)
                .isEighteenYearLimit(false)
                .organizerId(FAKER.number().randomNumber()).build();
        User organizer = mock(User.class);
        EventType eventType = mock(EventType.class);
        Event mappedRequestToEvent = mock(Event.class);
        Event savedMappedRequestToEventToDB = mock(Event.class);
        User spyUser = spy(User.class);
        List<User> allUsersIdWhoInterestingEventType = List.of(spyUser);
        DetailedEventInSearchDTO responseEventDto = mock(DetailedEventInSearchDTO.class);

        doReturn(organizer).when(userService).findUserByIdFetchUserAsOrganizer(anyLong());
        doReturn(eventType).when(eventTypeService).getEventTypeByIdFetchEventListWithTypeAndTagList(anyLong());
        doReturn(mappedRequestToEvent).when(eventMapper).mapToEvent(request, organizer, eventType, null,
                null, null, null);
        doReturn(savedMappedRequestToEventToDB).when(eventService).saveNewEvent(mappedRequestToEvent);
        doReturn(allUsersIdWhoInterestingEventType).when(userService).findAllByInterestingEventTypeContaining(anyLong());
        doReturn(FAKER.number().randomNumber()).when(spyUser).getId();
        doNothing().when(firebaseService).sendNotificationsToUsers(eq(allUsersIdWhoInterestingEventType), anyBoolean(),
                anyString(), anyString(), anyLong());
        doReturn(savedMappedRequestToEventToDB).when(eventService).getEventById(anyLong());
        doReturn(responseEventDto).when(eventMapper).mapToDetailedEvent(eq(savedMappedRequestToEventToDB), any());

        //when
        var actualResponseEventDto = eventFacade.handleNewEvent(request);

        //then
        assertThat(actualResponseEventDto).isNotNull();
        verify(eventMapper, times(1)).mapToEvent(request, organizer, eventType, null,
                null, null, null);
        verify(eventService, times(1)).saveNewEvent(mappedRequestToEvent);
        verify(eventMapper, times(1)).mapToDetailedEvent(eq(savedMappedRequestToEventToDB), any());
    }
}