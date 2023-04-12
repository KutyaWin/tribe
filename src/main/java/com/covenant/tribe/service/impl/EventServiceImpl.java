package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.EventDTO;
import com.covenant.tribe.dto.user.ParticipantPreviewDTO;
import com.covenant.tribe.exeption.event.EventNotFoundException;
import com.covenant.tribe.repository.EventRepository;
import com.covenant.tribe.service.EventService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;

    @Transactional
    @Override
    public EventDTO getEventById(Long eventId, Long userId) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> {
                    String message = String.format(
                            "Event with id %s  does not exist",
                            eventId
                    );
                    return new EventNotFoundException(message);
                });
        return mapEventEntityToEventDTO(event, userId);
    }

    private EventDTO mapEventEntityToEventDTO(Event event, Long userId) {
        EventDTO eventDTO = new EventDTO();
        User organizer = event.getOrganizer();
        EventAddress eventAddress = event.getEventAddress();
        List<User> participants = event.getUsersAsParticipantsEvent();
        eventDTO.setId(event.getId());
        eventDTO.setName(event.getEventName());
        eventDTO.setOrganizerNickname(organizer.getUsername());
        eventDTO.setOrganizerAvatarUrl(organizer.getUserAvatar());
        eventDTO.setEventPhotoUrl(event.getEventAvatar());
        eventDTO.setIsInUserFavoriteEvents(isEventInUserFavorites(userId, event.getUsersWhichAddedEventToFavorite()));
        eventDTO.setCity(eventAddress.getCity());
        eventDTO.setStartDate(getEventStartDate(event.getStartTime()));
        eventDTO.setStartTime(getEventStartTime(event.getStartTime()));
        eventDTO.setDescription(event.getEventDescription());
        eventDTO.setLatitude(eventAddress.getEventLatitude());
        eventDTO.setLongitude(eventAddress.getEventLongitude());
        eventDTO.setLostTenParticipantIds(getLostTenEventParticipants(participants));
        eventDTO.setParticipantsAmount(participants.size());
        eventDTO.setIsFinished(!event.isEventActive());
        return eventDTO;
    }

    private Boolean isEventInUserFavorites(Long userId, List<User> usersWhichAddedEventToFavorite) {
        if (userId == null) return false;
        return usersWhichAddedEventToFavorite.stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    private List<ParticipantPreviewDTO> getLostTenEventParticipants(List<User> participants) {
        ArrayList<ParticipantPreviewDTO> lostTenParticipants = new ArrayList<>();
        for (int i = 10, j = participants.size() - 1; i > 0; i--, j--) {
            if (j >= 0) {
                User currentParticipant = participants.get(j);
                ParticipantPreviewDTO participantPreviewDTO = new ParticipantPreviewDTO();
                participantPreviewDTO.setParticipantId(currentParticipant.getId());
                participantPreviewDTO.setParticipantAvatarUrl(currentParticipant.getUserAvatar());
                lostTenParticipants.add(participantPreviewDTO);
            } else {
                return lostTenParticipants;
            }
        }
        return lostTenParticipants;
    }

    private String getEventStartDate(LocalDateTime localDateTime) {
        DateTimeFormatter dateFormatterPattern = DateTimeFormatter.ofPattern("dd MMMM").localizedBy(new Locale("ru"));
        return localDateTime.format(dateFormatterPattern);
    }

    private String getEventStartTime(LocalDateTime localDateTime) {
        DateTimeFormatter dateFormatterPattern = DateTimeFormatter.ofPattern("HH:mm").localizedBy(new Locale("ru"));
        return localDateTime.format(dateFormatterPattern);
    }
}
