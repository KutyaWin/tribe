package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.UnknownUser;
import com.covenant.tribe.dto.user.UnknownUserWithInterestsDTO;
import com.covenant.tribe.exeption.event.EventTypeNotFoundException;
import com.covenant.tribe.repository.EventTypeRepository;
import com.covenant.tribe.repository.UnknownUserRepository;
import com.covenant.tribe.service.UnknownUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UnknownUserServiceImpl implements UnknownUserService {

    UnknownUserRepository unknownUserRepository;
    EventTypeRepository eventTypeRepository;

    @Override
    public Long saveNewUnknownUserWithInterests(UnknownUserWithInterestsDTO unknownUserWithInterests) {
        List<EventType> eventTypes = getEventTypes(unknownUserWithInterests.getEventTypeIds());
        UnknownUser unknownUser = unknownUserRepository.findUnknownUserByFirebaseId(
                unknownUserWithInterests.getFirebaseId()
                );
        if (unknownUser == null) {
            unknownUser = new UnknownUser();
            unknownUser.setFirebaseId(unknownUserWithInterests.getFirebaseId());
        }
        unknownUser.setUserInterests(eventTypes);
        unknownUserRepository.save(unknownUser);
        return unknownUser.getId();
    }

    private List<EventType> getEventTypes(List<Long> eventTypeIds) {
        return eventTypeIds.stream()
                .map(eventTypeId -> {
                    return eventTypeRepository.findById(eventTypeId)
                            .orElseThrow(() -> {
                                String message = String.format(
                                        "Event type with %s  does not exist",
                                        eventTypeId
                                );
                               throw new EventTypeNotFoundException(message);
                            });
                })
                .collect(Collectors.toList());
    }


}
