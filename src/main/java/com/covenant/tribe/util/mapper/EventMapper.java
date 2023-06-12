package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface EventMapper {

    Event mapToEvent(RequestTemplateForCreatingEventDTO dto, User organizer, EventType eventType,
                     @Nullable EventAddress eventAddress, @Nullable List<Tag> alreadyExistEventTags,
                     @Nullable List<Tag> createdEventTagsByRequest, @Nullable List<User> invitedUserByRequest);

    DetailedEventInSearchDTO mapToDetailedEvent(Event event, User currentUserWhoSendRequest);

    EventInFavoriteDTO mapToEventInFavoriteDTO(Event event);

    EventInUserProfileDTO mapToEventInUserProfileDTO(Event event, Long userId);

    EventVerificationDTO mapToEventVerificationDTO(Event event);

    List<SearchEventDTO> mapToSearchEventDTOList(List<Event> filteredEvents, List<UserRelationsWithEvent> relationsWithEventCurrentUserId);

    SearchEventDTO  mapToSearchEventDTO(Event event);

    SearchEventDTO  mapToSearchEventDTO(Event event, List<UserRelationsWithEvent> relationsWithEventCurrentUserId);
}
