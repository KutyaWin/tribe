package com.covenant.tribe.service;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.event.SearchEventDTO;
import com.covenant.tribe.dto.user.UserWhoInvitedToEventAsParticipantDTO;
import com.covenant.tribe.util.querydsl.EventFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public interface EventService {

    Page<SearchEventDTO> getEventsByFilter(EventFilter filter, Long currentUserId, Pageable pageable);

    Event saveEvent(Event event, Long organizerId);

    DetailedEventInSearchDTO handleNewEvent(RequestTemplateForCreatingEventDTO eventDto) throws FileNotFoundException;

    Event getEventById(Long eventId);

    DetailedEventInSearchDTO getDetailedEventById(Long eventId, Long userId);

    void addUserToEventAsParticipant(Long eventId, Long userId);

    Set<User> inviteUsersAsParticipantsToEvent(
            UserWhoInvitedToEventAsParticipantDTO userWhoInvitedToEventAsParticipantDTO, String eventId);
}
