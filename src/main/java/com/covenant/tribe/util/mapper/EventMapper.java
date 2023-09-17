package com.covenant.tribe.util.mapper;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.*;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.*;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import com.covenant.tribe.util.querydsl.PartsOfDay;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface EventMapper {

    Event mapToEvent(RequestTemplateForCreatingEventDTO dto, User organizer, EventType eventType,
                     List<EventContactInfo> eventContactInfos, @Nullable EventAddress eventAddress,
                     @Nullable List<Tag> alreadyExistEventTags, @Nullable List<Tag> createdEventTagsByRequest,
                     @Nullable List<User> invitedUserByRequest);

    Event mapToEvent(
            KudagoEventDto kudagoEventDto, User organizer, EventAddress eventAddress,
            EventType eventType, List<Tag> eventTags, UserRelationsWithEvent userRelationsWithEvent,
            ExternalEventDates externalEventDates, Boolean hasAgeRestriction, Set<EventAvatar> eventImages
    );

    DetailedEventInSearchDTO mapToDetailedEvent(Event event, User currentUserWhoSendRequest);

    EventInFavoriteDTO mapToEventInFavoriteDTO(Event event);

    EventInUserProfileDTO mapToEventInUserProfileDTO(Event event, Long userId);

    EventVerificationDTO mapToEventVerificationDTO(Event event);

    List<SearchEventDTO> mapToSearchEventDTOList(List<Event> filteredEvents, List<UserRelationsWithEvent> relationsWithEventCurrentUserId);

    SearchEventDTO mapToSearchEventDTO(Event event);

    SearchEventDTO mapToSearchEventDTO(Event event, List<UserRelationsWithEvent> relationsWithEventCurrentUserId);

    Set<EventPartOfDay> partEnumSetToEntity(Set<PartsOfDay> partsOfDay);

    Set<PartsOfDay> getPartsOfDay(Event event);

    EventPartOfDay partEnumToEntity(PartsOfDay partsOfDay);
}
