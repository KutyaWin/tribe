package com.covenant.tribe.service.impl;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.repository.UserRelationsWithEventRepository;
import com.covenant.tribe.service.UserRelationsWithEventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class UserRelationsWithEventServiceImpl implements UserRelationsWithEventService {

    UserRelationsWithEventRepository userRelationsWithEventRepository;

    @Override
    public UserRelationsWithEvent saveUserRelationsWithEvent(UserRelationsWithEvent userRelationsWithEvent) {
        return userRelationsWithEventRepository.save(userRelationsWithEvent);
    }

    @Override
    public UserRelationsWithEvent findUserRelationsWithEventByUserIdAndEventId(Long userId, Long eventId) {
        return userRelationsWithEventRepository.findByUserRelationsIdAndEventRelationsId(userId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "UserRelationsWithEvent with userId: " + userId + " and eventId: " + eventId + " not found")
                );
    }

    @Override
    public List<User> findParticipantsByEventId(Long eventId) {
        return userRelationsWithEventRepository
                .findByEventRelationsId(eventId).stream()
                .map(UserRelationsWithEvent::getUserRelations)
                .collect(Collectors.toList());
    }
}
