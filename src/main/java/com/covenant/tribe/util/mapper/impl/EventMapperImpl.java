package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.EventInFavoriteDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.event.SearchEventDTO;
import com.covenant.tribe.service.EventAddressService;
import com.covenant.tribe.service.EventTypeService;
import com.covenant.tribe.service.TagService;
import com.covenant.tribe.service.UserService;
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

        Event event = Event.builder()
                .organizer(userService.findUserByUsername(dto.getOrganizerUsername()))
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
        event.addUsersWhoInvitedToEvent(
                dto.getUsersWhoInvited().stream()
                        .map(userDTO -> userService.findUserByUsername(userDTO.getUsername()))
                        .collect(Collectors.toSet()));
        event.addTagSet(dto.getEventTagsNames().stream()
                .map(tagService::getTagOrSaveByTagName).collect(Collectors.toSet()));

        event.getOrganizer().addEventWhereUserAsOrganizer(event);
        event.getEventAddress().addEvent(event);
        event.getEventType().addEvent(event);

        return event;
    }

    @Override
    public DetailedEventInSearchDTO mapToDetailedEventInSearchDTO(Event event, Long userId) {
        log.debug("map Event to DetailedEventInSearchDTO. Event: {}", event);

        DetailedEventInSearchDTO detailedEvent = DetailedEventInSearchDTO.builder()
                .eventId(event.getId())
                .eventPhoto(event.getEventAvatar())
                .organizerPhoto(event.getOrganizer().getUserAvatar())
                .eventName(event.getEventName())
                .organizerUsername(event.getOrganizer().getUsername())
                .eventAddress(
                        eventAddressMapper.mapToEventAddressDTO(event.getEventAddress()))
                .startTime(event.getStartTime())
                .eventDuration(
                        Duration.between(event.getStartTime(), event.getEndTime()).toString())
                .description(event.getEventDescription())
                .usersWhoParticipantsOfEvent(
                        mapUsersToUsersWhoParticipantsOfEventDTO(event.getUsersAsParticipantsEvent()))
                .build();

        detailedEvent.setFavoriteEvent(
                userService.findUserById(userId).getFavoritesEvent().stream()
                        .map(Event::getId).anyMatch(id -> id.equals(event.getId())));

        return detailedEvent;
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
