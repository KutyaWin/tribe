package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.*;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.repository.TagRepository;
import com.covenant.tribe.repository.UserRelationsWithEventRepository;
import com.covenant.tribe.repository.UserRepository;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import com.covenant.tribe.util.mapper.EventAvatarMapper;
import com.covenant.tribe.util.mapper.EventMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class EventMapperImpl implements EventMapper {

    UserRepository userRepository;
    EventTypeRepository eventTypeRepository;
    EventAddressMapper eventAddressMapper;
    EventAvatarMapper eventAvatarMapper;
    TagRepository tagRepository;
    UserRelationsWithEventRepository userRelationsWithEventRepository;

    @Override
    public EventInFavoriteDTO mapToEventInFavoriteDTO(Event event) {
        log.info("map Event to EventInFavoriteDTO. Passed event: {}", event);
        List<String> eventAvatars = event.getEventAvatars().stream()
                .map(EventAvatar::getAvatarUrl)
                .toList();

        return EventInFavoriteDTO.builder()
                .eventId(event.getId())
                .eventPhoto(eventAvatars)
                .eventName(event.getEventName())
                .eventAddress(eventAddressMapper.mapToEventAddressDTO(event.getEventAddress()))
                .startTime(event.getStartTime())
                .isFinished(event.getEndTime().isBefore(OffsetDateTime.now()))
                .build();
    }

    private List<String> getEventAvatars(Set<EventAvatar> eventAvatars) {
        return eventAvatars.stream()
                .map(EventAvatar::getAvatarUrl)
                .toList();
    }

    private boolean isEventViewed(Event event, Long userId) {
        UserRelationsWithEvent currentUserRelations = event.getEventRelationsWithUser().stream()
                .filter(userRelationsWithEvent ->
                        userRelationsWithEvent
                                .getUserRelations()
                                .getId()
                                .equals(userId)
                )
                .findFirst()
                .orElseThrow(() -> {
                    String message = String.format(
                            "There isn't User with id %s in this event", userId
                    );
                    log.error(message);
                    return new UserNotFoundException(message);
                });
        return currentUserRelations.isViewed();
    }

    @Override
    public EventInUserProfileDTO mapToEventInUserProfileDTO(Event event) {
        return EventInUserProfileDTO.builder()
                .id(event.getId())
                .eventPhotoUrl(getEventAvatars(event.getEventAvatars()))
                .eventName(event.getEventName())
                .city(event.getEventAddress().getCity())
                .startTime(event.getStartTime())
                .isViewed(isEventViewed(event, event.getOrganizer().getId()))
                .build();
    }

    public Event mapToEvent(
            RequestTemplateForCreatingEventDTO dto) {
        log.debug("map RequestTemplateForCreatingEventDTO to Event. Passed dto: {}", dto);
        User organizer = userRepository
                .findUserById(dto.getOrganizerId())
                .orElseThrow(() -> {
                    String message = String.format(
                            "User with id %s didn't found", dto.getOrganizerId()
                    );
                    log.error(message);
                    return new UserNotFoundException(message);
                });
        EventAddress eventAddress = eventAddressMapper.mapToEventAddress(dto.getEventAddress());

        EventType eventType = eventTypeRepository
                .findById(dto.getEventTypeId())
                .orElseThrow(() -> {
                    String message = String.format(
                            "EventType with id %s did't found", dto.getEventTypeId()
                    );
                    log.error(message);
                    return new UserNotFoundException(message);
                });
        List<Tag> eventTags = tagRepository.findAllById(dto.getEventTagIds());


        Event event = Event.builder()
                .organizer(organizer)
                .eventAddress(eventAddress)
                .eventName(dto.getEventName())
                .eventDescription(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .showEventInSearch(dto.getShowEventInSearch())
                .sendToAllUsersByInterests(dto.getSendToAllUsersByInterests())
                .eighteenYearLimit(dto.getEighteenYearLimit())
                .isPrivate(dto.getIsPrivate())
                .eventType(eventType)
                .build();
        event.addTagList(eventTags);

        Set<EventAvatar> eventAvatars = dto.getAvatarsForAdding().stream()
                .map(avatar -> eventAvatarMapper.mapToEventAvatar(avatar, event))
                .collect(Collectors.toSet());

        List<User> invitedUsers = userRepository.findAllById(dto.getInvitedUserIds());


        List<UserRelationsWithEvent> userRelationsWithEvents = invitedUsers.stream()
                .map(user -> UserRelationsWithEvent.builder()
                        .userRelations(user)
                        .eventRelations(event)
                        .isInvited(true)
                        .isParticipant(false)
                        .isWantToGo(false)
                        .isFavorite(false)
                        .isViewed(false)
                        .build()
                ).collect(Collectors.toList());
        userRelationsWithEvents.add(
                UserRelationsWithEvent.builder()
                        .userRelations(organizer)
                        .eventRelations(event)
                        .isInvited(false)
                        .isParticipant(true)
                        .isWantToGo(false)
                        .isFavorite(false)
                        .isViewed(false)
                        .build()
        );
        event.setEventAvatars(eventAvatars);
        event.addEventsRelationsWithUsers(userRelationsWithEvents);
        event.getOrganizer().addEventWhereUserAsOrganizer(event);
        event.getEventAddress().addEvent(event);
        event.getEventType().addEvent(event);
        return event;
    }

    @Override
    public DetailedEventInSearchDTO mapToDetailedEventInSearchDTO(Event event, Long userId) {
        log.debug("map Event to DetailedEventInSearchDTO. Event: {}", event);
        List<String> eventAvatars = event.getEventAvatars().stream()
                .map(EventAvatar::getAvatarUrl)
                .toList();
        User currentUser = userRepository
                .findById(userId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "User with id %s didn't found", userId
                    );
                    log.error(message);
                    return new UserNotFoundException(message);
                });
        boolean isFavoriteEvent = currentUser.getUserRelationsWithEvents().stream()
                .filter(userRelationsWithEvent -> userRelationsWithEvent.getEventRelations().getId().equals(event.getId()))
                .findFirst()
                .map(UserRelationsWithEvent::isFavorite)
                .orElse(false);

        return DetailedEventInSearchDTO.builder()
                .eventId(event.getId())
                .eventPhoto(eventAvatars)
                .favoriteEvent(isFavoriteEvent)
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
                                        .filter(UserRelationsWithEvent::isParticipant)
                                        .map(UserRelationsWithEvent::getUserRelations)
                                        .collect(Collectors.toSet())))
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
