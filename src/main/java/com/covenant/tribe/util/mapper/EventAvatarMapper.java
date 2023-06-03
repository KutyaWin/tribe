package com.covenant.tribe.util.mapper;

import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.event.EventAvatar;
import com.covenant.tribe.domain.event.EventType;
import com.covenant.tribe.domain.user.User;
import com.covenant.tribe.dto.event.DetailedEventInSearchDTO;
import com.covenant.tribe.dto.event.EventInFavoriteDTO;
import com.covenant.tribe.dto.event.RequestTemplateForCreatingEventDTO;

public interface EventAvatarMapper {

    EventAvatar mapToEventAvatar(String avatarFileName);

}
