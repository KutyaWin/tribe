package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAvatar;

import java.util.List;
import java.util.Set;

public interface EventAvatarMapper {

    EventAvatar mapToEventAvatar(String avatarFileName, Event event);

    Set<EventAvatar> mapToEventAvatars(List<String> avatarFileNames);

}
