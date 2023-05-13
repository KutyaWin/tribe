package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.EventInUserProfileDTO;
import com.covenant.tribe.dto.event.EventVerificationDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.user.UserWhoInvitedToEventAsParticipantDTO;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public interface EventService {

    Event saveEvent(Event event, Long organizerId);

    DetailedEventInSearchDTO handleNewEvent(RequestTemplateForCreatingEventDTO eventDto) throws FileNotFoundException;

    Event getEventById(Long eventId);

    List<EventInUserProfileDTO> findEventsByOrganizerId(String organizerId);

    DetailedEventInSearchDTO getDetailedEventById(Long eventId, Long userId);

    void addUserToEventAsParticipant(Long eventId, Long userId);

    Set<User> inviteUsersAsParticipantsToEvent(
            UserWhoInvitedToEventAsParticipantDTO userWhoInvitedToEventAsParticipantDTO, String eventId);

    List<EventVerificationDTO> getEventWithVerificationPendingStatus();

    void updateEventStatusToPublished(Long eventId);

    void updateEventStatusToSendToRework(Long eventId);

    List<EventInUserProfileDTO> findEventsByUserIdWhichUserIsInvited(String userId);
}
