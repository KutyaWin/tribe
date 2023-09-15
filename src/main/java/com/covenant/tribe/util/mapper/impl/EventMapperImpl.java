package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.*;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.*;
import com.covenant.tribe.dto.event.external.ExternalEventDates;
import com.covenant.tribe.dto.user.UsersWhoParticipantsOfEventDTO;
import com.covenant.tribe.repository.EventPartOfDayRepository;
import com.covenant.tribe.util.mapper.EventAddressMapper;
import com.covenant.tribe.util.mapper.EventMapper;
import com.covenant.tribe.util.querydsl.PartsOfDay;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
public class EventMapperImpl implements EventMapper {

    EventAddressMapper eventAddressMapper;

    EventPartOfDayRepository eventPartOfDayRepository;

    @Override
    public List<SearchEventDTO> mapToSearchEventDTOList(List<Event> filteredEvents, List<UserRelationsWithEvent> relationsWithEventCurrentUserId) {

        return filteredEvents.stream()
                .map(event -> mapToSearchEventDTO(event, relationsWithEventCurrentUserId))
                .toList();
    }

    @Override
    @Transactional
    public SearchEventDTO mapToSearchEventDTO(Event event, List<UserRelationsWithEvent> relationsWithEventCurrentUserId) {

        if (event.isPrivate()) {
            return SearchEventDTO.builder()
                    .eventId(event.getId())
                    .organizerUsername(event.getOrganizer().getUsername())
                    .description(event.getEventDescription())
                    .avatarUrl(event.getEventAvatars().stream()
                            .map(EventAvatar::getAvatarUrl).toList())
                    .eventName(event.getEventName())
                    .eventType(event.getEventType().getTypeName())
                    .isFinished(isEventFinished(event))
                    .favoriteEvent(relationsWithEventCurrentUserId.stream()
                            .filter(UserRelationsWithEvent::isFavorite)
                            .anyMatch(relations -> relations.getEventRelations().equals(event)))
                    .isPresenceOfAlcohol(event.isPresenceOfAlcohol())
                    .isPrivate(true)
                    .isFree(event.isFree())
                    .build();
        }

        return SearchEventDTO.builder()
                .eventId(event.getId())
                .avatarUrl(event.getEventAvatars().stream()
                        .map(EventAvatar::getAvatarUrl).toList())
                .eventName(event.getEventName())
                .description(event.getEventDescription())
                .organizerUsername(event.getOrganizer().getUsername())
                .eventAddress(eventAddressMapper.mapToEventAddressDto(event.getEventAddress()))
                .startTime(event.getStartTime())
                .eventType(event.getEventType().getTypeName())
                .favoriteEvent(relationsWithEventCurrentUserId.stream()
                        .filter(UserRelationsWithEvent::isFavorite)
                        .anyMatch(relations -> relations.getEventRelations().equals(event)))
                .isFinished(isEventFinished(event))
                .isPrivate(event.isPrivate())
                .isFree(event.isFree())
                .isPresenceOfAlcohol(event.isPresenceOfAlcohol())
                .participants(mapUsersToUsersWhoParticipantsOfEventDTO(
                        event.getEventRelationsWithUser().stream()
                                .filter(UserRelationsWithEvent::isParticipant)
                                .map(UserRelationsWithEvent::getUserRelations)
                                .collect(Collectors.toSet())))
                .build();
    }

    private Boolean isEventFinished(Event event) {
        return event.getEndTime().isBefore(
                ZonedDateTime.now()
                        .withZoneSameInstant(
                                ZoneId.of(event.getTimeZone())
                        )
                        .toLocalDateTime()
        );
    }

    public SearchEventDTO mapToSearchEventDTO(Event event) {

        if (event.isPrivate()) {
            return SearchEventDTO.builder()
                    .eventId(event.getId())
                    .description(event.getEventDescription())
                    .organizerUsername(event.getOrganizer().getUsername())
                    .eventAddress(EventAddressDTO.builder()
                            .city(event.getEventAddress().getCity())
                            .region(event.getEventAddress().getRegion())
                            .build()
                    )
                    .avatarUrl(event.getEventAvatars().stream()
                            .map(EventAvatar::getAvatarUrl).toList())
                    .eventName(event.getEventName())
                    .eventType(event.getEventType().getTypeName())
                    .isPresenceOfAlcohol(event.isPresenceOfAlcohol())
                    .isPrivate(true)
                    .isFinished(isEventFinished(event))
                    .isFree(event.isFree())
                    .build();
        }
        var eventDto = SearchEventDTO.builder()
                .eventId(event.getId())
                .avatarUrl(event.getEventAvatars().stream()
                        .map(EventAvatar::getAvatarUrl).toList())
                .description(event.getEventDescription())
                .organizerUsername(event.getOrganizer().getUsername())
                .eventName(event.getEventName())
                .startTime(event.getStartTime())
                .eventType(event.getEventType().getTypeName())
                .isFinished(isEventFinished(event))
                .isPrivate(event.isPrivate())
                .isFree(event.isFree())
                .isPresenceOfAlcohol(event.isPresenceOfAlcohol())
                .participants(mapUsersToUsersWhoParticipantsOfEventDTO(
                        event.getEventRelationsWithUser().stream()
                                .filter(UserRelationsWithEvent::isParticipant)
                                .map(UserRelationsWithEvent::getUserRelations)
                                .collect(Collectors.toSet())))
                .build();
        if (event.getEventAddress() != null) {
            eventDto.setEventAddress(eventAddressMapper.mapToEventAddressDto(event.getEventAddress()));
        }
        return eventDto;
    }

    @Override
    public EventInFavoriteDTO mapToEventInFavoriteDTO(Event event) {
        List<String> eventAvatars = event.getEventAvatars().stream()
                .map(EventAvatar::getAvatarUrl)
                .toList();

        return EventInFavoriteDTO.builder()
                .eventId(event.getId())
                .eventPhoto(eventAvatars)
                .eventName(event.getEventName())
                .eventTypeName(event.getEventType().getTypeName())
                .eventAddress(EventAddressDTO.builder()
                        .city(event.getEventAddress().getCity())
                        .build()
                )
                .startTime(event.getStartTime())
                .isFinished(isEventFinished(event))
                .isDeleted(isEventDeleted(event))
                .build();
    }

    private Boolean isEventDeleted(Event event) {
        return event.getEventStatus().equals(EventStatus.DELETED);
    }

    private List<String> getEventAvatars(Set<EventAvatar> eventAvatars) {
        return eventAvatars.stream()
                .map(EventAvatar::getAvatarUrl)
                .toList();
    }

    @Override
    public EventInUserProfileDTO mapToEventInUserProfileDTO(Event event, Long userId) {
        return EventInUserProfileDTO.builder()
                .id(event.getId())
                .eventPhotoUrl(getEventAvatars(event.getEventAvatars()))
                .eventName(event.getEventName())
                .city(event.getEventAddress().getCity())
                .eventTypeName(event.getEventType().getTypeName())
                .startTime(event.getStartTime())
                .eventStatus(event.getEventStatus())
                .isFinished(isEventFinished(event))
                .build();
    }

    @Override
    public EventVerificationDTO mapToEventVerificationDTO(Event event) {
        return EventVerificationDTO.builder()
                .eventId(event.getId())
                .organizerId(event.getOrganizer().getId())
                .eventPhotos(getAvatars(event))
                .createdAt(event.getCreatedAt())
                .eventAddress(eventAddressMapper.mapToEventAddressDto(event.getEventAddress()))
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

    private List<String> getAvatars(Event event) {
        return event.getEventAvatars().stream()
                .map(EventAvatar::getAvatarUrl)
                .toList();
    }

    @Override
    public Event mapToEvent(RequestTemplateForCreatingEventDTO dto, User organizer,
                            EventType eventType, List<EventContactInfo> eventContactInfos,
                            @Nullable EventAddress eventAddress,
                            @Nullable List<Tag> alreadyExistEventTags,
                            @Nullable List<Tag> createdEventTagsByRequest,
                            @Nullable List<User> invitedUserByRequest) {

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
                .isPresenceOfAlcohol(dto.getHasAlcohol())
                .isFree(dto.getIsFree())
                .eventType(eventType)
                .timeZone(dto.getTimeZone())
                .build();
        event.setPartsOfDay(partEnumSetToEntity(getPartsOfDay(event)));
        organizer.addEventWhereUserAsOrganizer(event);
        eventType.addEvent(event);

        if (!eventContactInfos.isEmpty()) {
            event.addContactInfos(eventContactInfos);
        }

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
                            .build()
            );
            event.addEventsRelationsWithUsers(invitedUsersAndOrganizerRelationsWithEvent);
        }

        return event;
    }

    @Override
    public Event mapToEvent(
            KudagoEventDto kudagoEventDto, User organizer, EventAddress eventAddress,
            EventType eventType, List<Tag> eventTags,
            UserRelationsWithEvent userRelationsWithEvent, ExternalEventDates externalEventDates,
            Boolean hasAgeRestriction, Set<EventAvatar> eventImages
    ) {
        Long kudaGoEventID = kudagoEventDto.getId();
        Event event = Event.builder()
                .organizer(organizer)
                .eventStatus(EventStatus.PUBLISHED)
                .eventName(kudagoEventDto.getTitle())
                .eventDescription(kudagoEventDto.getBodyText())
                .startTime(externalEventDates.start())
                .kudaGoId(kudaGoEventID)
                .isFromKudaGo(true)
                .externalPublicationDate(externalEventDates.publicationDate())
                .endTime(externalEventDates.end())
                .showEventInSearch(true)
                .sendToAllUsersByInterests(false)
                .isEighteenYearLimit(kudagoEventDto.getAgeRestriction() != null)
                .isPrivate(false)
                .isPresenceOfAlcohol(hasAgeRestriction)
                .isFree(kudagoEventDto.getIsFree())
                .eventType(eventType)
                .timeZone(kudagoEventDto.getLocation().timezone)
                .build();
        event.setPartsOfDay(partEnumSetToEntity(getPartsOfDay(event)));
        organizer.addEventWhereUserAsOrganizer(event);
        eventType.addEvent(event);

        if (eventAddress != null) {
            event.setEventAddress(eventAddress);
            eventAddress.addEvent(event);
        }
        event.addTagList(eventTags);
        event.addEventRelationsWithUser(userRelationsWithEvent);
        event.addEventAvatars(eventImages);
        return event;
    }

    @Override
    public DetailedEventInSearchDTO mapToDetailedEvent(Event event, User currentUserWhoSendRequest) {

        List<String> eventAvatars = event.getEventAvatars().stream()
                .map(EventAvatar::getAvatarUrl)
                .toList();
        boolean isFavoriteEvent = false;
        boolean isParticipant = false;
        boolean isWantToGo = false;
        boolean isInvited = false;
        if (currentUserWhoSendRequest != null) {
            isFavoriteEvent = currentUserWhoSendRequest.getUserRelationsWithEvents().stream()
                    .filter(userRelationsWithEvent -> userRelationsWithEvent.getEventRelations().getId().equals(event.getId()))
                    .findFirst()
                    .map(UserRelationsWithEvent::isFavorite)
                    .orElse(false);
            isParticipant = currentUserWhoSendRequest.getUserRelationsWithEvents().stream()
                    .filter(userRelationsWithEvent -> {
                        return userRelationsWithEvent.getEventRelations().getId().equals(event.getId());
                    })
                    .findFirst()
                    .map(UserRelationsWithEvent::isParticipant)
                    .orElse(false);
            isWantToGo = currentUserWhoSendRequest.getUserRelationsWithEvents().stream()
                    .filter(userRelationsWithEvent -> {
                        return userRelationsWithEvent.getEventRelations().getId().equals(event.getId());
                    })
                    .findFirst()
                    .map(UserRelationsWithEvent::isWantToGo)
                    .orElse(false);
            isInvited = currentUserWhoSendRequest.getUserRelationsWithEvents().stream()
                    .filter(userRelationsWithEvent -> {
                        return userRelationsWithEvent.getEventRelations().getId().equals(event.getId());
                    })
                    .findFirst()
                    .map(UserRelationsWithEvent::isInvited)
                    .orElse(false);
            if (event.isPrivate() && isParticipant) {
                return makeDetailedEventForParticipantOrNoPrivateEvent(
                        event, eventAvatars, isFavoriteEvent, isParticipant, isInvited, isWantToGo
                );
            }
            if (event.isPrivate()) {
                return makeDetailedEventForNoParticipantAndPrivateEvent(
                        event, eventAvatars, isFavoriteEvent, isParticipant, isInvited, isWantToGo
                );
            }
        }
        if (event.isPrivate()) {
            return makeDetailedEventForNoParticipantAndPrivateEvent(
                    event, eventAvatars, isFavoriteEvent,
                    isParticipant, isInvited, isWantToGo
            );
        }
        return makeDetailedEventForParticipantOrNoPrivateEvent(
                event, eventAvatars, isFavoriteEvent, isParticipant, isInvited, isWantToGo
        );
    }

    private DetailedEventInSearchDTO makeDetailedEventForNoParticipantAndPrivateEvent(
            Event event, List<String> eventAvatars, boolean isFavoriteEvent,
            boolean isParticipant, boolean isInvited, boolean isWantToGo
    ) {
        return DetailedEventInSearchDTO.builder()
                .eventId(event.getId())
                .eventPhoto(eventAvatars)
                .favoriteEvent(isFavoriteEvent)
                .organizerPhoto(event.getOrganizer().getUserAvatar())
                .organizerUsername(event.getOrganizer().getUsername())
                .organizerId(event.getOrganizer().getId())
                .eventAddress(
                        EventAddressDTO.builder()
                                .city(event.getEventAddress().getCity())
                                .region(event.getEventAddress().getRegion())
                                .build()
                )
                .eventName(event.getEventName())
                .eventTypeName(event.getEventType().getTypeName())
                .contactInfos(getContactInfo(event.getEventContactInfos()))
                .description(event.getEventDescription())
                .isPrivate(event.isPrivate())
                .isFree(event.isFree())
                .isParticipant(isParticipant)
                .isInvited(isInvited)
                .isWantToGo(isWantToGo)
                .build();
    }
    private DetailedEventInSearchDTO makeDetailedEventForParticipantOrNoPrivateEvent(
            Event event, List<String> eventAvatars, boolean isFavoriteEvent, boolean isParticipant,
            boolean isInvited, boolean isWantToGo
    ) {
        var responseDto = DetailedEventInSearchDTO.builder()
                .eventId(event.getId())
                .eventPhoto(eventAvatars)
                .favoriteEvent(isFavoriteEvent)
                .organizerPhoto(event.getOrganizer().getUserAvatar())
                .eventName(event.getEventName())
                .eventTypeName(event.getEventType().getTypeName())
                .contactInfos(getContactInfo(event.getEventContactInfos()))
                .organizerUsername(event.getOrganizer().getUsername())
                .organizerId(event.getOrganizer().getId())
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
                .isFree(event.isFree())
                .isFinished(isEventFinished(event))
                .isParticipant(isParticipant)
                .isInvited(isInvited)
                .isWantToGo(isWantToGo)
                .build();
        if (event.getEventAddress() != null) {
            responseDto.setEventAddress(eventAddressMapper.mapToEventAddressDto(event.getEventAddress()));
        }
        return responseDto;
    }

    private List<EventContactInfoDto> getContactInfo(List<EventContactInfo> eventContactInfos) {
        return eventContactInfos.stream()
                .map(eventContactInfo -> {
                    return EventContactInfoDto.builder()
                            .contact(eventContactInfo.getContact())
                            .contactType(eventContactInfo.getContactType())
                            .build();
                })
                .toList();
    }

    private Set<UsersWhoParticipantsOfEventDTO> mapUsersToUsersWhoParticipantsOfEventDTO(Set<User> users) {

        return users.stream()
                .map(user -> UsersWhoParticipantsOfEventDTO.builder()
                        .participantId(user.getId())
                        .participantAvatarUrl(user.getUserAvatar())
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public EventPartOfDay partEnumToEntity(PartsOfDay partsOfDay) {
        return eventPartOfDayRepository.findByPartsOfDay(partsOfDay.ordinal())
                .orElseThrow(() -> new EntityNotFoundException("Part of day not found"));
    }

    @Override
    public Set<EventPartOfDay> partEnumSetToEntity(Set<PartsOfDay> partsOfDay) {
        return partsOfDay.stream().map(this::partEnumToEntity).collect(Collectors.toSet());
    }

    @Override
    public Set<PartsOfDay> getPartsOfDay(Event event) {
        LocalDateTime startTime = event.getStartTime();
        LocalDateTime trunc = startTime.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endTime = event.getEndTime();
        Set<PartsOfDay> newParts = new HashSet<>();
        int passedDays = 0;
        while (trunc.isBefore(endTime) && newParts.size() < 4 && passedDays < 3) {
            if (passedDays == 0) {
                trunc = trunc.minusDays(1); //to check yesterday 23 - today 06
            }
            for (PartsOfDay part : PartsOfDay.values()) {
                int cur = Integer.parseInt(part.getHour());
                int next = Integer.parseInt(PartsOfDay.getNextEnumValue(part).getHour());
                if (next < cur) {
                    next += 24;
                }
                LocalDateTime lb = trunc.plusHours(cur);
                LocalDateTime hb = trunc.plusHours(next);
                if (((lb.isBefore(startTime) || lb.isEqual(startTime)) && startTime.isBefore(hb))  //time period and dates are crossing
                        || (lb.isBefore(endTime) && (endTime.isBefore(hb) || endTime.isEqual(hb))) //
                        || (startTime.isBefore(lb) && hb.isBefore(endTime)) //time period is between events start and end time
                ) {
                    newParts.add(part);
                }
            }
            trunc = trunc.plusDays(1); //to check today 23 - today + n days 06
            passedDays += 1;
        }

        return newParts;
    }
}
