package com.covenant.tribe.chat.factory;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.domain.event.Event;
import com.covenant.tribe.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ChatFactory {

    public Chat makeChat(Set<User> participants, Boolean isGroup, Event event) {
        return Chat.builder()
                .participant(participants)
                .isGroup(isGroup)
                .event(event)
                .build();
    }

}
