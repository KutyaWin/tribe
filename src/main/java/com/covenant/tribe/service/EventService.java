package com.covenant.tribe.service;

import com.covenant.tribe.client.dadata.dto.ReverseGeocodingData;
import com.covenant.tribe.client.kudago.dto.KudagoEventDto;
import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventIdView;
import com.covenant.tribe.dto.event.*;
import com.covenant.tribe.util.querydsl.EventFilter;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface EventService {

    Pair<Predicate, Pageable> getPredicateForFilters(EventFilter filter, Long currentUserId, Integer page, Integer size);

    Event saveNewEvent(Event event);

    Event saveNewExternalEvent(
            List<KudagoEventDto> externalEvents,
            Map<Long, ReverseGeocodingData> reverseGeocodingData,
            Map<Long, List<String>> images
    );

    Event save(Event event);

    Event getEventById(Long eventId);

    List<EventInUserProfileDTO> findEventsByOrganizerId(String organizerId, Long requestUserId);
    Page<Event> getByIdIn(List<Long> ids, Pageable pageable);

    Page<Event> getAll(Pageable pageable, Predicate predicate);

    List<UserRelationsWithEvent> getUserRelationsWithEvents(Long currentUserId);

    DetailedEventInSearchDTO getDetailedEventById(Long eventId, Long userId);

    void addUsersToPrivateEventAsParticipants(Long eventId, Long userId);

    Page<Event> findAll(Pageable pageable, Predicate predicate);

    List<Event> findAll(Integer page, Integer size);

    List<Event> findAllByEventStatusIs(Integer page, Integer size);

    List<EventIdView> findIdsByPredicate(Predicate predicate);

    List<EventVerificationDTO> getEventWithVerificationPendingStatus();

    void updateEventStatusToPublished(Long eventId);

    void updateEventStatusToSendToRework(Long eventId);

    List<EventInUserProfileDTO> findEventsByUserIdWhichUserIsInvited(String userId);

    List<EventInUserProfileDTO> findEventsByUserIdWhichUserIsParticipant(String userId);

    void confirmInvitationToEvent(Long eventId, String userId);

    void declineInvitationToEvent(Long eventId, String userId);

    void declineToParticipantInEvent(Long eventId, String userId);

    void deleteEvent(Long organizerId, Long eventId);

    void sendToOrganizerRequestToParticipationInPrivateEvent(Long eventId, String userId);

    void sendRequestToParticipationInPublicEvent(Long eventId, String userId);

    void addUserToPrivateEventAsParticipant(Long eventId, Long organizerId, Long userId);

    void saveEventToFavorite(Long userId, Long eventId);

    List<Event> getAllFavoritesByUserId(Long userId);

    void removeEventFromFavorite(Long userId, Long eventId);

    boolean isFavoriteEventForUser(Long userId, Long eventId);

    EventDto getEventForUpdating(Long eventId, Long organizerId);

    DetailedEventInSearchDTO updateEvent(UpdateEventDto updateEventDto) throws IOException;

    void updatePartsOfDay();

    List<Event> getByIdIn(List<Long> collect);
    void withdrawalRequestToParticipateInPrivateEvent(Long eventId, Long userId);

    FilteredEventQuantityDto getFilteredEventQuantity(EventFilter eventFilter);
}
