package com.covenant.tribe.chat.factory;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.domain.Message;
import com.covenant.tribe.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MessageFactory {

    public Message makeMessage(String text, User author, Chat chat) {
        return Message.builder()
                .text(text)
                .author(author)
                .chat(chat)
                .build();
    }
}
