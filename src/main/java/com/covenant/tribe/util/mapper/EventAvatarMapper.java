package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAvatar;

public interface EventAvatarMapper {

    EventAvatar mapToEventAvatar(String avatarFileName, Event event);

}
