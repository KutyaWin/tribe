package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.user.UserWhoInvitedToEventAsParticipantDTO;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface EventService {

    Event saveEvent(Event event);

    DetailedEventInSearchDTO saveNewEvent(RequestTemplateForCreatingEventDTO eventDto);

    Event getEventById(Long eventId);

    DetailedEventInSearchDTO getDetailedEventById(Long eventId, Long userId);

    void addUserToEventAsParticipant(Long eventId, Long userId);

    Set<User> inviteUsersAsParticipantsToEvent(
            UserWhoInvitedToEventAsParticipantDTO userWhoInvitedToEventAsParticipantDTO, String eventId);
}
