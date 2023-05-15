package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.*;

import java.util.List;

public interface EventMapper {

    Event mapToEvent(
            RequestTemplateForCreatingEventDTO requestTemplateForCreatingEventDTO
    );
    DetailedEventInSearchDTO mapToDetailedEventInSearchDTO(Event event, Long userId);

    EventInFavoriteDTO mapToEventInFavoriteDTO(Event event);

    EventInUserProfileDTO mapToEventInUserProfileDTO(Event event);

    EventVerificationDTO mapToEventVerificationDTO(Event event);

    List<SearchEventDTO> mapToSearchEventDTOList(List<Event> filteredEvents, List<UserRelationsWithEvent> relationsWithEventCurrentUserId);

    SearchEventDTO  mapToSearchEventDTO(Event event);

    SearchEventDTO  mapToSearchEventDTO(Event event, List<UserRelationsWithEvent> relationsWithEventCurrentUserId);
}
