package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.*;
import com.covenant.tribe.dto.user.UsersWhoParticipantsOfEventDTO;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import com.covenant.tribe.util.mapper.EventMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class EventMapperImpl implements EventMapper {

    EventAddressMapper eventAddressMapper;

    @Override
    public List<SearchEventDTO> mapToSearchEventDTOList(List<Event> filteredEvents, List<UserRelationsWithEvent> relationsWithEventCurrentUserId) {
        log.info("map List<Event> to List<SearchEventDTO>");
        log.debug("Passed List<Event>: {}", filteredEvents);

        return filteredEvents.stream()
                .map(event -> mapToSearchEventDTO(event, relationsWithEventCurrentUserId))
                .toList();
    }

    @Override
    public SearchEventDTO mapToSearchEventDTO(Event event, List<UserRelationsWithEvent> relationsWithEventCurrentUserId) {
        log.info("map Event to SearchEventDTO");
        log.debug("Passed Event: {}", event);

        if (event.isPrivate()) {
            return SearchEventDTO.builder()
                    .eventId(event.getId())
                    .avatarUrl(event.getEventAvatars().stream()
                            .map(EventAvatar::getAvatarUrl).toList())
                    .eventName(event.getEventName())
                    .eventType(event.getEventType().getTypeName())
                    .viewEvent(relationsWithEventCurrentUserId.stream()
                            .filter(UserRelationsWithEvent::isViewed)
                            .anyMatch(relations -> relations.getEventRelations().equals(event)))
                    .favoriteEvent(relationsWithEventCurrentUserId.stream()
                            .filter(UserRelationsWithEvent::isFavorite)
                            .anyMatch(relations -> relations.getEventRelations().equals(event)))
                    .isPrivate(true)
                    .build();
        }

        return SearchEventDTO.builder()
                .eventId(event.getId())
                .avatarUrl(event.getEventAvatars().stream()
                        .map(EventAvatar::getAvatarUrl).toList())
                .eventName(event.getEventName())
                .eventAddress(eventAddressMapper.mapToEventAddressDTO(event.getEventAddress()))
                .startTime(event.getStartTime().toLocalDateTime())
                .eventType(event.getEventType().getTypeName())
                .favoriteEvent(relationsWithEventCurrentUserId.stream()
                        .filter(UserRelationsWithEvent::isFavorite)
                        .anyMatch(relations -> relations.getEventRelations().equals(event)))
                .viewEvent(relationsWithEventCurrentUserId.stream()
                        .filter(UserRelationsWithEvent::isViewed)
                        .anyMatch(relations -> relations.getEventRelations().equals(event)))
                .isPrivate(event.isPrivate())
                .build();
    }

    public SearchEventDTO mapToSearchEventDTO(Event event) {
        log.info("map Event to SearchEventDTO");
        log.debug("Passed Event: {}", event);

        if (event.isPrivate()) {
            return SearchEventDTO.builder()
                    .eventId(event.getId())
                    .avatarUrl(event.getEventAvatars().stream()
                            .map(EventAvatar::getAvatarUrl).toList())
                    .eventName(event.getEventName())
                    .eventType(event.getEventType().getTypeName())
                    .isPrivate(true)
                    .build();
        }
        var eventDto = SearchEventDTO.builder()
                .eventId(event.getId())
                .avatarUrl(event.getEventAvatars().stream()
                        .map(EventAvatar::getAvatarUrl).toList())
                .eventName(event.getEventName())
                .startTime(event.getStartTime().toLocalDateTime())
                .eventType(event.getEventType().getTypeName())
                .isPrivate(event.isPrivate())
                .build();
        if (event.getEventAddress() != null) {
            eventDto.setEventAddress(eventAddressMapper.mapToEventAddressDTO(event.getEventAddress()));
        }
        return eventDto;
    }

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
    public EventInUserProfileDTO mapToEventInUserProfileDTO(Event event, Long userId) {
        return EventInUserProfileDTO.builder()
                .id(event.getId())
                .eventPhotoUrl(getEventAvatars(event.getEventAvatars()))
                .eventName(event.getEventName())
                .city(event.getEventAddress().getCity())
                .startTime(event.getStartTime())
                .isViewed(isEventViewed(event, userId))
                .build();
    }

    @Override
    public EventVerificationDTO mapToEventVerificationDTO(Event event) {
        return EventVerificationDTO.builder()
                .eventId(event.getId())
                .organizerId(event.getOrganizer().getId())
                .createdAt(event.getCreatedAt())
                .eventAddress(eventAddressMapper.mapToEventAddressDTO(event.getEventAddress()))
                .eventName(event.getEventName())
                .eventDescription(event.getEventDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .eighteenYearLimit(event.isEighteenYearLimit())
                .eventType(event.getEventType().getTypeName())
                .eventTags(getEventTagNames(event.getTagList()))
                .build();
    }

    private List<String> getEventTagNames(List<Tag> eventTags) {
        return eventTags.stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
    }

    @Override
    public Event mapToEvent(RequestTemplateForCreatingEventDTO dto, User organizer,
                            EventType eventType, @Nullable EventAddress eventAddress,
                            @Nullable List<Tag> alreadyExistEventTags,
                            @Nullable List<Tag> createdEventTagsByRequest,
                            @Nullable List<User> invitedUserByRequest)
    {
        log.debug("map RequestTemplateForCreatingEventDTO to Event. Passed param: {},\n {},\n {},\n {},\n {},\n {},\n {}",
                dto, organizer, eventType, eventAddress, alreadyExistEventTags, createdEventTagsByRequest, invitedUserByRequest);

        Event event = Event.builder()
                .organizer(organizer)
                .eventName(dto.getEventName())
                .eventDescription(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .showEventInSearch(dto.getShowEventInSearch())
                .sendToAllUsersByInterests(dto.getSendToAllUsersByInterests())
                .isEighteenYearLimit(dto.getIsEighteenYearLimit())
                .isPrivate(dto.getIsPrivate())
                .eventType(eventType)
                .build();

        organizer.addEventWhereUserAsOrganizer(event);
        eventType.addEvent(event);

        if (eventAddress != null) {
            event.setEventAddress(eventAddress);
            eventAddress.addEvent(event);
        }
        if (alreadyExistEventTags != null) {
            event.addTagList(alreadyExistEventTags);
        }
        if (createdEventTagsByRequest != null) {
            event.addTagList(createdEventTagsByRequest);
        }
        if (invitedUserByRequest != null) {
            List<UserRelationsWithEvent> invitedUsersAndOrganizerRelationsWithEvent = new ArrayList<>();
            invitedUsersAndOrganizerRelationsWithEvent.addAll(
                    invitedUserByRequest.stream()
                            .map(user -> UserRelationsWithEvent.builder()
                                    .userRelations(user)
                                    .eventRelations(event)
                                    .isInvited(true)
                                    .isParticipant(false)
                                    .isWantToGo(false)
                                    .isFavorite(false)
                                    .isViewed(false)
                                    .build())
                            .toList());
            invitedUsersAndOrganizerRelationsWithEvent.add(
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
            event.addEventsRelationsWithUsers(invitedUsersAndOrganizerRelationsWithEvent);
        }

        return event;
    }

    @Override
    public DetailedEventInSearchDTO mapToDetailedEvent(Event event, User currentUserWhoSendRequest) {
        log.debug("map Event to DetailedEventInSearchDTO. Event: {}", event);

        List<String> eventAvatars = event.getEventAvatars().stream()
                .map(EventAvatar::getAvatarUrl)
                .toList();
        boolean isFavoriteEvent = currentUserWhoSendRequest.getUserRelationsWithEvents().stream()
                .filter(userRelationsWithEvent -> userRelationsWithEvent.getEventRelations().getId().equals(event.getId()))
                .findFirst()
                .map(UserRelationsWithEvent::isFavorite)
                .orElse(false);

        if (event.isPrivate()) {
            return DetailedEventInSearchDTO.builder()
                    .eventId(event.getId())
                    .eventPhoto(eventAvatars)
                    .favoriteEvent(isFavoriteEvent)
                    .eventName(event.getEventName())
                    .description(event.getEventDescription())
                    .isPrivate(event.isPrivate())
                    .build();
        }
        var responseDto = DetailedEventInSearchDTO.builder()
                .eventId(event.getId())
                .eventPhoto(eventAvatars)
                .favoriteEvent(isFavoriteEvent)
                .organizerPhoto(event.getOrganizer().getUserAvatar())
                .eventName(event.getEventName())
                .organizerUsername(event.getOrganizer().getUsername())
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
                .isPrivate(event.isPrivate())
                .build();
        if (event.getEventAddress() != null) {
            responseDto.setEventAddress(eventAddressMapper.mapToEventAddressDTO(event.getEventAddress()));
        }
        return responseDto;
    }

    private Set<UsersWhoParticipantsOfEventDTO> mapUsersToUsersWhoParticipantsOfEventDTO(Set<User> users) {
        log.debug("map Users to UsersWhoParticipantsOfEventDTO. Passed users: {}", users);

        return users.stream()
                .map(user -> UsersWhoParticipantsOfEventDTO.builder()
                        .participantId(user.getId())
                        .participantAvatarUrl(user.getUserAvatar())
                        .build())
                .collect(Collectors.toSet());
    }
}
