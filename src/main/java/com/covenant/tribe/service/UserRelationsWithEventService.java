package com.covenant.tribe.service;

import com.covenant.tribe.domain.UserRelationsWithEvent;
import org.springframework.stereotype.Service;

@Service
public interface UserRelationsWithEventService {
    UserRelationsWithEvent saveUserRelationsWithEvent(UserRelationsWithEvent userRelationsWithEvent);

    UserRelationsWithEvent findUserRelationsWithEventByUserIdAndEventId(Long userId, Long eventId);
}
