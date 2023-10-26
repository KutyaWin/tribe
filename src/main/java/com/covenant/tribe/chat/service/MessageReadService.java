package com.covenant.tribe.chat.service;

import com.covenant.tribe.chat.domain.Chat;
import com.covenant.tribe.chat.domain.LastReadMessage;
import com.covenant.tribe.domain.user.User;

public interface MessageReadService {
    void sendMessageToSubscribers(User userWhoRead, Chat chatWhenRead, LastReadMessage readMessage);
}
