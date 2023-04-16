package com.covenant.tribe.mapper;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.EventDTO;
import com.covenant.tribe.dto.user.ParticipantPreviewDTO;
import com.covenant.tribe.dto.user.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EventMapper {
    public EventDTO mapEventToEventDTO(Event event, Long userId) {
        log.debug("map Event to EventDTO. Passed User: {}", event);
        return EventDTO.builder()
                .id(event.getId())
                .name(event.getEventName())
                .organizerNickname(event.getOrganizer().getUsername())
                .organizerAvatarUrl(event.getOrganizer().getUserAvatar())
                .eventPhotoUrl(event.getEventAvatar())
                .isInUserFavoriteEvents(
                        isEventInUserFavorites(userId, event.getUsersWhichAddedEventToFavorite())
                )
                .city(event.getEventAddress().getCity())
                .startDate(event.getStartTime().toString())
                .description(event.getEventDescription())
                .latitude(event.getEventAddress().getEventLatitude())
                .longitude(event.getEventAddress().getEventLongitude())
                .lostTenParticipantIds(getLostTenEventParticipants(event.getUsersAsParticipantsEvent()))
                .participantsAmount(event.getUsersAsParticipantsEvent().size())
                .isFinished(!event.isEventActive())
                .build();
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
}
