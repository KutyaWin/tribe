package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.domain.user.UserStatus;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.EventInFavoriteDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.event.SearchEventDTO;
import com.covenant.tribe.service.*;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import com.covenant.tribe.util.mapper.EventMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class EventMapperImpl implements EventMapper {

    UserService userService;
    EventTypeService eventTypeService;
    TagService tagService;
    EventAddressService eventAddressService;
    EventAddressMapper eventAddressMapper;
    UserRelationsWithEventService userRelationsWithEventService;

    @Override
    public EventInFavoriteDTO mapToEventInFavoriteDTO(Event event) {
        log.info("map Event to EventInFavoriteDTO. Passed event: {}", event);

        return EventInFavoriteDTO.builder()
                .eventId(event.getId())
                .eventPhoto(event.getEventAvatar())
                .eventName(event.getEventName())
                .eventAddress(eventAddressMapper.mapToEventAddressDTO(event.getEventAddress()))
                .startTime(event.getStartTime())
                .isFinished(event.getEndTime().isBefore(LocalDateTime.now()))
                .build();
    }

    public Event mapToEvent(
            RequestTemplateForCreatingEventDTO dto) {
        log.debug("map RequestTemplateForCreatingEventDTO to Event. Passed dto: {}", dto);

        User organizer = userService.findUserByUsername(dto.getOrganizerUsername());
        Event event = Event.builder()
                .organizer(organizer)
                .eventName(dto.getEventName())
                .eventDescription(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .eventAvatar(dto.getEventPhoto())
                .showEventInSearch(dto.getShowEventInSearch())
                .sendToAllUsersByInterests(dto.getSendToAllUsersByInterests())
                .eighteenYearLimit(dto.getEighteenYearLimit())
                .eventType(eventTypeService.getEventTypeByName(dto.getEventTypeName()))
                .build();
        event.setEventAddress(
                eventAddressService.saveNewEventAddress(
                        eventAddressMapper.mapToEventAddress(dto.getEventAddress())));
        event.addTagSet(dto.getEventTagsNames().stream()
                .map(tagService::getTagOrSaveByTagName).collect(Collectors.toSet()));

        event.addEventsRelationsWithUsers(
                dto.getUsersWhoInvited().stream()
                        .map(user -> userRelationsWithEventService.saveUserRelationsWithEvent(
                                UserRelationsWithEvent.builder()
                                        .user(organizer)
                                        .event(event)
                                        .userStatus(UserStatus.INVITED_TO_EVENT)
                                        .build()
                        )).toList());

        event.getOrganizer().addEventWhereUserAsOrganizer(event);
        event.getEventAddress().addEvent(event);
        event.getEventType().addEvent(event);

        return event;
    }

    @Override
    public DetailedEventInSearchDTO mapToDetailedEventInSearchDTO(Event event, Long userId) {
        log.debug("map Event to DetailedEventInSearchDTO. Event: {}", event);

        return DetailedEventInSearchDTO.builder()
                .eventId(event.getId())
                .eventPhoto(event.getEventAvatar())
                .favoriteEvent(userService.isFavoriteEventForUser(userId, event.getId()))
                .organizerPhoto(event.getOrganizer().getUserAvatar())
                .eventName(event.getEventName())
                .organizerUsername(event.getOrganizer().getUsername())
                .eventAddress(eventAddressMapper.mapToEventAddressDTO(event.getEventAddress()))
                .startTime(event.getStartTime())
                .eventDuration(
                        Duration.between(event.getStartTime(), event.getEndTime()).toString())
                .description(event.getEventDescription())
                .usersWhoParticipantsOfEvent(
                        mapUsersToUsersWhoParticipantsOfEventDTO(
                                event.getEventRelationsWithUser().stream()
                                        .filter(userRelationsWithEvent -> userRelationsWithEvent.getUserStatus().equals(UserStatus.PARTICIPANT_OF_EVENT))
                                        .map(UserRelationsWithEvent::getUser).collect(Collectors.toSet())))
                .build();
    }

    private Set<SearchEventDTO.UsersWhoParticipantsOfEventDTO> mapUsersToUsersWhoParticipantsOfEventDTO(Set<User> users) {
        log.debug("map Users to UsersWhoParticipantsOfEventDTO. Passed users: {}", users);

        return users.stream()
                .map(user -> SearchEventDTO.UsersWhoParticipantsOfEventDTO.builder()
                        .participantId(user.getId())
                        .participantAvatarUrl(user.getUserAvatar())
                        .build())
                .collect(Collectors.toSet());
    }
}
