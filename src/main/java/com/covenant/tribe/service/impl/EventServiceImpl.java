package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;
import com.covenant.tribe.dto.user.UserWhoInvitedToEventAsParticipantDTO;
import com.covenant.tribe.exeption.event.EventNotFoundException;
import com.covenant.tribe.exeption.user.UserNotFoundException;
import com.covenant.tribe.util.mapper.EventMapper;
import com.covenant.tribe.repository.EventRepository;
import com.covenant.tribe.repository.UserRepository;
import com.covenant.tribe.service.EventService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;
    EventMapper eventMapper;

    @Transactional
    @Override
    public Event saveNewEvent(RequestTemplateForCreatingEventDTO eventDto) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        Event event = eventMapper.mapToEvent(eventDto);
        event = saveEvent(event);

        log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
        return event;
    }

    @Transactional(readOnly = true)
    public DetailedEventInSearchDTO getDetailedEventById(Long eventId, Long userId) {
        log.info("[TRANSACTION] Open transaction in class: " + this.getClass().getName());

        Event event = getEventById(eventId);
        DetailedEventInSearchDTO detailedEventInSearchDTO = eventMapper.mapToDetailedEventInSearchDTO(event, userId);

        log.info("[TRANSACTION] End transaction in class: " + this.getClass().getName());
        return detailedEventInSearchDTO;
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("[EXCEPTION] event with id {}, does not exist", eventId);
                    return new EventNotFoundException(String.format("Event with id %s  does not exist", eventId));
                });
    }

    @Transactional
    @Override
    public Set<User> inviteUsersAsParticipantsToEvent(
            UserWhoInvitedToEventAsParticipantDTO userWhoInvitedToEventAsParticipantDTO, String eventId) {

        //todo: refactor method
        return null;
    }

    @Transactional
    @Override
    public void addUserToEventAsParticipant(Long eventId, Long userId) {
        /*Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format(
                                "Event with id %s  does not exist",
                                eventId)
                ));
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(
                                "User with id %s  does not exist",
                                eventId)
                ));
        event.addUserAsAsParticipantsEvent(user);*/
    }
}
