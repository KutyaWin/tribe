package com.covenant.tribe.service.impl.pojo;

import com.covenant.tribe.domain.Tag;
import com.covenant.tribe.domain.event.EventAddress;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;

import java.util.List;

public record CollectedDataForMappingToEvent(
        User organizer,
        EventType eventType,
        EventAddress eventAddress,
        List<Tag> alreadyExistEventTags,
        List<Tag> createdEventTagsByRequest,
        List<User> invitedUserByRequest) {
}
