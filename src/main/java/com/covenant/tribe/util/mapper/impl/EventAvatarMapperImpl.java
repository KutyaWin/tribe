package com.covenant.tribe.util.mapper.impl;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.util.mapper.EventAvatarMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EventAvatarMapperImpl implements EventAvatarMapper {
    @Override
    public EventAvatar mapToEventAvatar(String avatarFileName, Event event) {
        return EventAvatar.builder()
                .avatarUrl(avatarFileName)
                .event(event)
                .build();
    }

    @Override
    public Set<EventAvatar> mapToEventAvatars(List<String> avatarFileNames) {
        return avatarFileNames.stream()
                .map(avatarFileName -> {
                    return EventAvatar.builder()
                            .avatarUrl(avatarFileName)
                            .event(null)
                            .build();
                })
                .collect(Collectors.toSet());
    }

}
