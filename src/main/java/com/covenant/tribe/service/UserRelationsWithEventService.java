package com.covenant.tribe.service;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import com.covenant.tribe.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserRelationsWithEventService {
    UserRelationsWithEvent saveUserRelationsWithEvent(UserRelationsWithEvent userRelationsWithEvent);

    UserRelationsWithEvent findUserRelationsWithEventByUserIdAndEventId(Long userId, Long eventId);

    List<User> findParticipantsByEventId(Long eventId);
}
