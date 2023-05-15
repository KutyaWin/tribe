package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.EventInFavoriteDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.event.SearchEventDTO;
import com.covenant.tribe.dto.user.UsersWhoParticipantsOfEventDTO;
import com.covenant.tribe.exeption.event.EventNotFoundException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.repository.TagRepository;
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
import java.util.List;
import java.util.Objects;
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

    @Override
    public List<SearchEventDTO> mapToSearchEventDTOList(List<Event> filteredEvents, Long currentUserId) {
        log.info("map List<Event> to List<SearchEventDTO>");
        log.debug("Passed List<Event>: {}", filteredEvents);

        return filteredEvents.stream()
                .map(event -> mapToSearchEventDTO(event, currentUserId))
                .toList();
    }

    @Override
    public SearchEventDTO mapToSearchEventDTO(Event event, Long currentUserId) {
        log.info("map Event to SearchEventDTO");
        log.debug("Passed Event: {}", event);

        SearchEventDTO searchEventDTO = SearchEventDTO.builder()
                .eventId(event.getId())
                .avatarUrl(event.getEventAvatars().stream()
                        .map(EventAvatar::getAvatarUrl).toList())
                .eventName(event.getEventName())
                .eventAddress(eventAddressMapper.mapToEventAddressDTO(event.getEventAddress()))
                .startTime(event.getStartTime())
                .favoriteEvent(false)
                .build();

        if (currentUserId != null) {
            List<Event> favoriteEventsCurrentUser = userRepository.findUserById(currentUserId)
                    .orElseThrow(() -> {
                        String message = String.format(
                                "[EXCEPTION] User with id %s, dont exist", currentUserId
                        );
                        log.error(message);
                        return new UserNotFoundException(message);
                    }).getUserRelationsWithEvents().stream()
                    .filter(UserRelationsWithEvent::isFavorite)
                    .map(UserRelationsWithEvent::getEventRelations)
                    .toList();
            searchEventDTO.setFavoriteEvent(favoriteEventsCurrentUser.contains(event));
        }

        return searchEventDTO;
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
                .isFinished(event.getEndTime().isBefore(LocalDateTime.now()))
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
                .isEighteenYearLimit(dto.getEighteenYearLimit())
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
                        .userRelations(organizer)
                        .eventRelations(event)
                        .isInvited(true)
                        .isParticipant(false)
                        .isWantToGo(false)
                        .isFavorite(false)
                        .isViewed(false)
                        .build()
                ).toList();
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
