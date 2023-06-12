package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.util.mapper.EventAvatarMapper;
import org.springframework.stereotype.Component;

@Component
public class EventAvatarMapperImpl implements EventAvatarMapper {
    @Override
    public EventAvatar mapToEventAvatar(String avatarFileName) {
        return EventAvatar.builder()
                .avatarUrl(avatarFileName)
                .build();
    }
}
