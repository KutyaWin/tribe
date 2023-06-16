package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.dto.event.*;
import com.covenant.tribe.util.querydsl.EventFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {

    Page<SearchEventDTO> getEventsByFilter(EventFilter filter, Long currentUserId, Pageable pageable);

    Event saveNewEvent(Event event);

    Event save(Event event);

    Event getEventById(Long eventId);

    List<EventInUserProfileDTO> findEventsByOrganizerId(String organizerId);

    DetailedEventInSearchDTO getDetailedEventById(Long eventId, Long userId);

    void addUsersToPrivateEventAsParticipants(Long eventId, Long userId);

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

    EventDto getEvent(Long eventId, Long organizerId);

    DetailedEventInSearchDTO updateEvent(UpdateEventDto updateEventDto);
}
